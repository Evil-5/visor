#!/usr/bin/env bash
# Bash Security Scan Runner for Docker/Podman
# Scans: Checkov, Gitleaks, OSV-Scanner, Semgrep, Trivy Code, TruffleHog, Trivy Image

set -euo pipefail

echo "===================================================="
echo "       SPDX Custom Security Agent Scan Runner        "
echo "===================================================="

ENGINE=""
if command -v docker &> /dev/null; then
    ENGINE="docker"
elif command -v podman &> /dev/null; then
    ENGINE="podman"
else
    echo "Error: Neither 'docker' nor 'podman' was found in your PATH." >&2
    exit 1
fi
echo "Using container engine: $ENGINE"

TARGET_DIR="${1:-}"
JOB_ID="${2:-}"
if [ -z "$TARGET_DIR" ]; then
    echo "Error: Target directory must be provided." >&2
    exit 1
fi
echo "Target Directory: $TARGET_DIR"

MOUNT_PATH="${TARGET_DIR//\\//}"

# We can't use local paths easily in DooD, so we'll just save audits in /tmp/security_audit inside the backend container
AUDIT_DIR="/tmp/security_audit_${JOB_ID:-default}"
ERROR_DIR="$AUDIT_DIR/error"
mkdir -p "$AUDIT_DIR" "$ERROR_DIR"
echo "Audit reports will be saved to: $AUDIT_DIR"

report_status() {
    local scanner="$1"
    local status="$2"
    if [ -n "$JOB_ID" ] && [ -n "${VINDICATOR_CONTROL_PLANE_URL:-}" ]; then
        curl -s -X PATCH "$VINDICATOR_CONTROL_PLANE_URL/api/v1/scans/jobs/$JOB_ID/status" \
            -H "Content-Type: application/json" \
            -d "{\"scannerName\":\"$scanner\",\"status\":\"$status\"}" || true
    fi
}

run_checkov() {
    echo "[+] Starting Checkov scan..."
    report_status "CHECKOV" "RUNNING"
    local scan_dir="$AUDIT_DIR/checkov"
    mkdir -p "$scan_dir"
    
    # We omit config logic for simplicity in DooD context unless configs are mounted
    if $ENGINE run --rm -v "$MOUNT_PATH:/tf" bridgecrew/checkov:latest -d "/tf" -o json > "$scan_dir/checkov-report.json"; then
        echo "[OK] Checkov scan finished."
        report_status "CHECKOV" "COMPLETED"
    else
        echo "[FAIL] Checkov scan failed."
        report_status "CHECKOV" "FAILED"
    fi
}

run_gitleaks() {
    echo "[+] Starting Gitleaks scan..."
    report_status "GITLEAKS" "RUNNING"
    local scan_dir="$AUDIT_DIR/gitleaks"
    mkdir -p "$scan_dir"
    
    # Output to stdout and pipe to avoid writing to host volume
    if $ENGINE run --rm -v "$MOUNT_PATH:/path" zricethezav/gitleaks:latest detect --source="/path" --report-format json --report-path=/dev/stdout --no-git > "$scan_dir/gitleaks-report.json"; then
        echo "[OK] Gitleaks scan finished."
        report_status "GITLEAKS" "COMPLETED"
    else
        echo "[FAIL] Gitleaks scan failed."
        report_status "GITLEAKS" "FAILED"
    fi
}

run_osv() {
    echo "[+] Starting OSV-Scanner scan..."
    report_status "OSV-SCANNER" "RUNNING"
    local scan_dir="$AUDIT_DIR/osv-scanner"
    mkdir -p "$scan_dir"
    
    if $ENGINE run --rm -v "$MOUNT_PATH:/src" ghcr.io/google/osv-scanner:latest scan --format json -r "/src" > "$scan_dir/osv-report.json"; then
        echo "[OK] OSV-Scanner scan finished."
        report_status "OSV-SCANNER" "COMPLETED"
    else
        echo "[FAIL] OSV-Scanner scan failed."
        report_status "OSV-SCANNER" "FAILED"
    fi
}

run_semgrep() {
    echo "[+] Starting Semgrep scan..."
    report_status "SEMGREP" "RUNNING"
    local scan_dir="$AUDIT_DIR/semgrep"
    mkdir -p "$scan_dir"
    
    if $ENGINE run --rm -v "$MOUNT_PATH:/src" returntocorp/semgrep:latest semgrep scan --json --config=auto --exclude=".venv" --exclude="node_modules" > "$scan_dir/semgrep-report.json"; then
        echo "[OK] Semgrep scan finished."
        report_status "SEMGREP" "COMPLETED"
    else
        echo "[FAIL] Semgrep scan failed."
        report_status "SEMGREP" "FAILED"
    fi
}

run_trivy() {
    echo "[+] Starting Trivy Code scan..."
    report_status "TRIVY" "RUNNING"
    local scan_dir="$AUDIT_DIR/trivy"
    mkdir -p "$scan_dir"
    
    if $ENGINE run --rm -v "$MOUNT_PATH:/src" aquasec/trivy:latest fs --format json "/src" > "$scan_dir/trivy-report.json"; then
        echo "[OK] Trivy scan finished."
        report_status "TRIVY" "COMPLETED"
    else
        echo "[FAIL] Trivy scan failed."
        report_status "TRIVY" "FAILED"
    fi
}

run_trufflehog() {
    echo "[+] Starting TruffleHog scan..."
    report_status "TRUFFLEHOG" "RUNNING"
    local scan_dir="$AUDIT_DIR/trufflehog"
    mkdir -p "$scan_dir"
    
    if $ENGINE run --rm -v "$MOUNT_PATH:/pwd" trufflesecurity/trufflehog:latest filesystem "/pwd" --json --only-verified > "$scan_dir/trufflehog-report.json"; then
        echo "[OK] TruffleHog scan finished."
        report_status "TRUFFLEHOG" "COMPLETED"
    else
        echo "[FAIL] TruffleHog scan failed."
        report_status "TRUFFLEHOG" "FAILED"
    fi
}

echo "Running all code & secret scanners sequentially..."
run_checkov
run_gitleaks
run_osv
run_semgrep
run_trivy
run_trufflehog

echo "===================================================="
echo "All scans completed! Reports stored in: $AUDIT_DIR"
echo "===================================================="

if [ -n "${VINDICATOR_CONTROL_PLANE_URL:-}" ]; then
    echo "Ingesting reports to backend..."
    
    # We need jq to stringify json for the payload
    apk add --no-cache jq > /dev/null 2>&1 || true

    for report in $(find "$AUDIT_DIR" -name "*.json"); do
        scanner=$(basename $(dirname "$report"))
        scanner_upper=$(echo "$scanner" | tr '[:lower:]' '[:upper:]')
        
        # Check if report has contents
        if [ -s "$report" ]; then
            # We can use python to create the JSON payload if jq is missing
            cat <<EOF > /tmp/payload.json
{
  "asset": "$TARGET_DIR",
  "scanner": "$scanner_upper",
  "rawReportJson": $(cat "$report" | sed 's/\\/\\\\/g' | sed 's/"/\\"/g' | tr -d '\n' | awk '{print "\"" $0 "\""}')
}
EOF
            # The above awk/sed hack is fragile for huge json. A better way in python:
            python3 -c "import sys, json; print(json.dumps({'asset': '$TARGET_DIR', 'scanner': '$scanner_upper', 'rawReportJson': open('$report').read()}))" > /tmp/payload.json || true
            
            echo "Uploading $scanner_upper report..."
            curl -s -X POST "$VINDICATOR_CONTROL_PLANE_URL/api/v1/scans/ingest" \
                 -H "Content-Type: application/json" \
                 -d @/tmp/payload.json || true
        fi
    done
fi
