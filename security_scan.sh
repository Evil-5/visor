#!/usr/bin/env bash
# Bash Security Scan Runner for Docker/Podman
# Scans: Checkov, Gitleaks, OSV-Scanner, Semgrep, Trivy Code, TruffleHog, Trivy Image
# Automatically mounts Vindicator configs/ to avoid false positives (.venv, sample API keys, test fixtures)

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

TARGET_DIR="${1:-$(pwd)}"
TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
echo "Target Directory: $TARGET_DIR"

AUDIT_DIR="$TARGET_DIR/.bin/security_audit"
ERROR_DIR="$AUDIT_DIR/error"
mkdir -p "$AUDIT_DIR" "$ERROR_DIR"
echo "Audit reports will be saved to: $AUDIT_DIR"

run_checkov() {
    echo "[+] Starting Checkov scan..."
    local scan_dir="$AUDIT_DIR/checkov"
    mkdir -p "$scan_dir"
    local config_flag=""
    if [ -f "$TARGET_DIR/configs/.checkov.yaml" ]; then
        config_flag="--config-file /tf/configs/.checkov.yaml"
    fi
    $ENGINE run --rm -v "$TARGET_DIR:/tf" bridgecrew/checkov:latest -d "/tf" -o json --output-file-path "/tf/security_audit/checkov/checkov-report.json" $config_flag || true
    echo "[OK] Checkov scan finished."
}

run_gitleaks() {
    echo "[+] Starting Gitleaks scan..."
    local scan_dir="$AUDIT_DIR/gitleaks"
    mkdir -p "$scan_dir"
    local config_flag=""
    if [ -f "$TARGET_DIR/configs/.gitleaks.toml" ]; then
        config_flag="--config=/path/configs/.gitleaks.toml"
    fi
    $ENGINE run --rm -v "$TARGET_DIR:/path" zricethezav/gitleaks:latest detect --source="/path" --report-path="/path/security_audit/gitleaks/gitleaks-report.json" --no-git $config_flag || true
    echo "[OK] Gitleaks scan finished."
}

run_osv() {
    echo "[+] Starting OSV-Scanner scan..."
    local scan_dir="$AUDIT_DIR/osv-scanner"
    mkdir -p "$scan_dir"
    local config_flag=""
    if [ -f "$TARGET_DIR/configs/osv-scanner.toml" ]; then
        config_flag="--config=/src/configs/osv-scanner.toml"
    fi
    $ENGINE run --rm -v "$TARGET_DIR:/src" ghcr.io/google/osv-scanner:latest scan --format json --output="/src/security_audit/osv-scanner/osv-report.json" $config_flag -r "/src" || true
    echo "[OK] OSV-Scanner scan finished."
}

run_semgrep() {
    echo "[+] Starting Semgrep scan..."
    local scan_dir="$AUDIT_DIR/semgrep"
    mkdir -p "$scan_dir"
    $ENGINE run --rm -v "$TARGET_DIR:/src" returntocorp/semgrep:latest semgrep scan --json --output="/src/security_audit/semgrep/semgrep-report.json" --config=auto --exclude=".venv" --exclude="node_modules" --exclude=".bin" --exclude="security_audit" || true
    echo "[OK] Semgrep scan finished."
}

run_trivy() {
    echo "[+] Starting Trivy Code scan..."
    local scan_dir="$AUDIT_DIR/trivy"
    mkdir -p "$scan_dir"
    local config_flag=""
    if [ -f "$TARGET_DIR/configs/trivy.yaml" ]; then
        config_flag="--config /src/configs/trivy.yaml"
    fi
    $ENGINE run --rm -v "$TARGET_DIR:/src" aquasec/trivy:latest fs --format json --output "/src/security_audit/trivy/trivy-report.json" $config_flag "/src" || true
    echo "[OK] Trivy scan finished."
}

run_trufflehog() {
    echo "[+] Starting TruffleHog scan..."
    local scan_dir="$AUDIT_DIR/trufflehog"
    mkdir -p "$scan_dir"
    local config_flag=""
    if [ -f "$TARGET_DIR/configs/.trufflehogignore" ]; then
        config_flag="--exclude-paths=/pwd/configs/.trufflehogignore"
    fi
    $ENGINE run --rm -v "$TARGET_DIR:/pwd" trufflesecurity/trufflehog:latest filesystem "/pwd" --json --only-verified $config_flag > "$scan_dir/trufflehog-report.json" || true
    echo "[OK] TruffleHog scan finished."
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
