# PowerShell Security Scan Runner for Docker/Podman
# Scans: Checkov, Gitleaks, OSV-Scanner, Semgrep, Trivy Code, TruffleHog, Trivy Image

$ErrorActionPreference = "Stop"

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "       SPDX Custom Security Agent Scan Runner        " -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

# 1. Detect Container Engine
$Engine = ""
if (Get-Command docker -ErrorAction SilentlyContinue) {
    $Engine = "docker"
} elseif (Get-Command podman -ErrorAction SilentlyContinue) {
    $Engine = "podman"
} else {
    Write-Host "Error: Neither 'docker' nor 'podman' was found in your PATH." -ForegroundColor Red
    Write-Host "Please install Docker or Podman to run the security scans." -ForegroundColor Yellow
    Exit 1
}
Write-Host "Using container engine: $Engine" -ForegroundColor Green

# 2. Prompt for Codebase Directory
$TargetDir = Read-Host "Enter the absolute path of the codebase to scan (Press Enter for current directory)"
if ([string]::IsNullOrWhiteSpace($TargetDir)) {
    $TargetDir = Get-Location
}

# Resolve target directory to absolute Windows path
$TargetDir = (Resolve-Path $TargetDir).Path
if (-not (Test-Path -Path $TargetDir -PathType Container)) {
    Write-Host "Error: Directory '$TargetDir' does not exist." -ForegroundColor Red
    Exit 1
}
Write-Host "Target Directory: $TargetDir" -ForegroundColor Green

# Create disposable scan output under the target's .bin folder.
# This keeps reports out of source folders and works with each repository's ignore rules.
$AuditDir = Join-Path $TargetDir ".bin\security_audit"
if (-not (Test-Path -Path $AuditDir)) {
    New-Item -ItemType Directory -Force -Path $AuditDir | Out-Null
}
$ErrorDir = Join-Path $AuditDir "error"
if (-not (Test-Path -Path $ErrorDir)) {
    New-Item -ItemType Directory -Force -Path $ErrorDir | Out-Null
}
Write-Host "Audit reports will be saved to subfolders in: $AuditDir" -ForegroundColor Green
Write-Host "Error logs will be saved in: $ErrorDir" -ForegroundColor Green

# Standardize path for Docker mounting on Windows (convert backslashes to forward slashes for volume mount compatibility)
$MountPath = $TargetDir.Replace('\', '/')

# Define Scanner Script Blocks
$CheckovBlock = {
    param($Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag)
    $ErrorActionPreference = "Continue"
    $ScanName = "checkov"
    $Image = "bridgecrew/checkov:latest"
    $ScanDir = Join-Path $AuditDir $ScanName
    $ReportFile = Join-Path $ScanDir "checkov-report.json"
    $ErrorFile = Join-Path $ErrorDir "${ScanName}.txt"

    if (Test-Path $ReportFile) { Remove-Item $ReportFile -Force }
    if (Test-Path $ErrorFile) { Remove-Item $ErrorFile -Force }
    if (-not (Test-Path $ScanDir)) { New-Item -ItemType Directory -Force -Path $ScanDir | Out-Null }

    Write-Output "[+] Starting Checkov scan (Image: $Image)..."
    & $Engine pull $Image 2>&1 | Out-String | Out-Null

    try {
        $ConfigArg = if (Test-Path (Join-Path $TargetDir "configs\.checkov.yaml")) { "--config-file /tf/configs/.checkov.yaml" } else { "" }
        $output = & $Engine run --rm -v "${MountPath}:/tf" $Image -d "/tf" -o json --output-file-path "/tf/security_audit/checkov/checkov-report.json" $ConfigArg 2>&1 | Out-String
        $ExitCode = $LASTEXITCODE
        if ($ExitCode -eq 0 -or $ExitCode -eq 1) {
            Write-Output "[OK] Checkov scan completed. Report: security_audit/checkov/checkov-report.json"
        } else {
            Write-Output "[FAIL] Checkov scan exited with code $ExitCode"
            $output | Out-File -FilePath $ErrorFile -Encoding utf8
        }
    } catch {
        Write-Output "[FAIL] Checkov scan failed: $_"
        $_ | Out-String | Out-File -FilePath $ErrorFile -Encoding utf8
    }
}

$GitleaksBlock = {
    param($Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag)
    $ErrorActionPreference = "Continue"
    $ScanName = "gitleaks"
    $Image = "zricethezav/gitleaks:latest"
    $ScanDir = Join-Path $AuditDir $ScanName
    $ReportFile = Join-Path $ScanDir "gitleaks-report.json"
    $ErrorFile = Join-Path $ErrorDir "${ScanName}.txt"

    if (Test-Path $ReportFile) { Remove-Item $ReportFile -Force }
    if (Test-Path $ErrorFile) { Remove-Item $ErrorFile -Force }
    if (-not (Test-Path $ScanDir)) { New-Item -ItemType Directory -Force -Path $ScanDir | Out-Null }

    Write-Output "[+] Starting Gitleaks scan (Image: $Image)..."
    & $Engine pull $Image 2>&1 | Out-String | Out-Null

    try {
        $ConfigArg = if (Test-Path (Join-Path $TargetDir "configs\.gitleaks.toml")) { "--config=/path/configs/.gitleaks.toml" } else { "" }
        $output = & $Engine run --rm -v "${MountPath}:/path" $Image detect --source="/path" --report-path="/path/security_audit/gitleaks/gitleaks-report.json" --no-git $ConfigArg 2>&1 | Out-String
        $ExitCode = $LASTEXITCODE
        if ($ExitCode -eq 0 -or $ExitCode -eq 1) {
            Write-Output "[OK] Gitleaks scan completed. Report: security_audit/gitleaks/gitleaks-report.json"
        } else {
            Write-Output "[FAIL] Gitleaks scan exited with code $ExitCode"
            $output | Out-File -FilePath $ErrorFile -Encoding utf8
        }
    } catch {
        Write-Output "[FAIL] Gitleaks scan failed: $_"
        $_ | Out-String | Out-File -FilePath $ErrorFile -Encoding utf8
    }
}

$OsvBlock = {
    param($Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag)
    $ErrorActionPreference = "Continue"
    $ScanName = "osv-scanner"
    $Image = "ghcr.io/google/osv-scanner:latest"
    $ScanDir = Join-Path $AuditDir $ScanName
    $ReportFile = Join-Path $ScanDir "osv-report.json"
    $ErrorFile = Join-Path $ErrorDir "${ScanName}.txt"

    if (Test-Path $ReportFile) { Remove-Item $ReportFile -Force }
    if (Test-Path $ErrorFile) { Remove-Item $ErrorFile -Force }
    if (-not (Test-Path $ScanDir)) { New-Item -ItemType Directory -Force -Path $ScanDir | Out-Null }

    Write-Output "[+] Starting OSV-Scanner scan (Image: $Image)..."
    & $Engine pull $Image 2>&1 | Out-String | Out-Null

    try {
        $ConfigArg = if (Test-Path (Join-Path $TargetDir "configs\osv-scanner.toml")) { "--config=/src/configs/osv-scanner.toml" } else { "" }
        $output = & $Engine run --rm -v "${MountPath}:/src" $Image scan --format json --output="/src/security_audit/osv-scanner/osv-report.json" $ConfigArg -r "/src" 2>&1 | Out-String
        $ExitCode = $LASTEXITCODE
        if ($ExitCode -eq 0 -or $ExitCode -eq 1) {
            Write-Output "[OK] OSV-Scanner scan completed. Report: security_audit/osv-scanner/osv-report.json"
        } else {
            Write-Output "[FAIL] OSV-Scanner scan exited with code $ExitCode"
            $output | Out-File -FilePath $ErrorFile -Encoding utf8
        }
    } catch {
        Write-Output "[FAIL] OSV-Scanner scan failed: $_"
        $_ | Out-String | Out-File -FilePath $ErrorFile -Encoding utf8
    }
}

$SemgrepBlock = {
    param($Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag)
    $ErrorActionPreference = "Continue"
    $ScanName = "semgrep"
    $Image = "returntocorp/semgrep:latest"
    $ScanDir = Join-Path $AuditDir $ScanName
    $ReportFile = Join-Path $ScanDir "semgrep-report.json"
    $ErrorFile = Join-Path $ErrorDir "${ScanName}.txt"

    if (Test-Path $ReportFile) { Remove-Item $ReportFile -Force }
    if (Test-Path $ErrorFile) { Remove-Item $ErrorFile -Force }
    if (-not (Test-Path $ScanDir)) { New-Item -ItemType Directory -Force -Path $ScanDir | Out-Null }

    Write-Output "[+] Starting Semgrep scan (Image: $Image)..."
    & $Engine pull $Image 2>&1 | Out-String | Out-Null

    try {
        $output = & $Engine run --rm -v "${MountPath}:/src" $Image semgrep scan --json --output="/src/security_audit/semgrep/semgrep-report.json" --config=auto --exclude=".venv" --exclude="node_modules" --exclude=".bin" --exclude="security_audit" 2>&1 | Out-String
        $ExitCode = $LASTEXITCODE
        if ($ExitCode -eq 0) {
            Write-Output "[OK] Semgrep scan completed. Report: security_audit/semgrep/semgrep-report.json"
        } else {
            Write-Output "[FAIL] Semgrep scan exited with code $ExitCode"
            $output | Out-File -FilePath $ErrorFile -Encoding utf8
        }
    } catch {
        Write-Output "[FAIL] Semgrep scan failed: $_"
        $_ | Out-String | Out-File -FilePath $ErrorFile -Encoding utf8
    }
}

$TrivyBlock = {
    param($Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag)
    $ErrorActionPreference = "Continue"
    $ScanName = "trivy"
    $Image = "aquasec/trivy:latest"
    $ScanDir = Join-Path $AuditDir $ScanName
    $ReportFile = Join-Path $ScanDir "trivy-report.json"
    $ErrorFile = Join-Path $ErrorDir "${ScanName}.txt"

    if (Test-Path $ReportFile) { Remove-Item $ReportFile -Force }
    if (Test-Path $ErrorFile) { Remove-Item $ErrorFile -Force }
    if (-not (Test-Path $ScanDir)) { New-Item -ItemType Directory -Force -Path $ScanDir | Out-Null }

    Write-Output "[+] Starting Trivy Code scan (Image: $Image)..."
    & $Engine pull $Image 2>&1 | Out-String | Out-Null

    try {
        $ConfigArg = if (Test-Path (Join-Path $TargetDir "configs\trivy.yaml")) { "--config /src/configs/trivy.yaml" } else { "" }
        $output = & $Engine run --rm -v "${MountPath}:/src" $Image fs --format json --output "/src/security_audit/trivy/trivy-report.json" $ConfigArg "/src" 2>&1 | Out-String
        $ExitCode = $LASTEXITCODE
        if ($ExitCode -eq 0) {
            Write-Output "[OK] Trivy Code scan completed. Report: security_audit/trivy/trivy-report.json"
        } else {
            Write-Output "[FAIL] Trivy Code scan exited with code $ExitCode"
            $output | Out-File -FilePath $ErrorFile -Encoding utf8
        }
    } catch {
        Write-Output "[FAIL] Trivy Code scan failed: $_"
        $_ | Out-String | Out-File -FilePath $ErrorFile -Encoding utf8
    }
}

$TrufflehogBlock = {
    param($Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag)
    $ErrorActionPreference = "Continue"
    $ScanName = "trufflehog"
    $Image = "trufflesecurity/trufflehog:latest"
    $ScanDir = Join-Path $AuditDir $ScanName
    $ReportFile = Join-Path $ScanDir "trufflehog-report.json"
    $ErrorFile = Join-Path $ErrorDir "${ScanName}.txt"

    if (Test-Path $ReportFile) { Remove-Item $ReportFile -Force }
    if (Test-Path $ErrorFile) { Remove-Item $ErrorFile -Force }
    if (-not (Test-Path $ScanDir)) { New-Item -ItemType Directory -Force -Path $ScanDir | Out-Null }

    Write-Output "[+] Starting TruffleHog scan (Image: $Image)..."
    & $Engine pull $Image 2>&1 | Out-String | Out-Null

    try {
        $ConfigArg = if (Test-Path (Join-Path $TargetDir "configs\.trufflehogignore")) { "--exclude-paths=/pwd/configs/.trufflehogignore" } else { "" }
        $output = & $Engine run --rm -v "${MountPath}:/pwd" $Image filesystem "/pwd" --json --only-verified $ConfigArg 2>&1 | Out-String
        $ExitCode = $LASTEXITCODE
        if ($ExitCode -eq 0 -or $ExitCode -eq 1) {
            $output | Out-File -FilePath $ReportFile -Encoding utf8
            Write-Output "[OK] TruffleHog scan completed. Report: security_audit/trufflehog/trufflehog-report.json"
        } else {
            Write-Output "[FAIL] TruffleHog scan exited with code $ExitCode"
            $output | Out-File -FilePath $ErrorFile -Encoding utf8
        }
    } catch {
        Write-Output "[FAIL] TruffleHog scan failed: $_"
        $_ | Out-String | Out-File -FilePath $ErrorFile -Encoding utf8
    }
}

$TrivyImageBlock = {
    param($Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag)
    $ErrorActionPreference = "Continue"
    $ScanName = "trivy-image"
    $Image = "aquasec/trivy:latest"
    $ScanDir = Join-Path $AuditDir $ScanName
    $ReportFile = Join-Path $ScanDir "trivy-image-report.json"
    $ErrorFile = Join-Path $ErrorDir "${ScanName}.txt"

    if (Test-Path $ReportFile) { Remove-Item $ReportFile -Force }
    if (Test-Path $ErrorFile) { Remove-Item $ErrorFile -Force }
    if (-not (Test-Path $ScanDir)) { New-Item -ItemType Directory -Force -Path $ScanDir | Out-Null }

    if ([string]::IsNullOrWhiteSpace($ImageTag)) {
        Write-Output "[SKIP] Trivy Image scan skipped: No image tag specified."
        return
    }

    Write-Output "[+] Starting Trivy Image scan on image: $ImageTag (Image: $Image)..."
    & $Engine pull $Image 2>&1 | Out-String | Out-Null

    try {
        $output = & $Engine run --rm -v /var/run/docker.sock:/var/run/docker.sock -v "${MountPath}:/src" $Image image --format json --output "/src/security_audit/trivy-image/trivy-image-report.json" $ImageTag 2>&1 | Out-String
        $ExitCode = $LASTEXITCODE
        if ($ExitCode -eq 0) {
            Write-Output "[OK] Trivy Image scan completed. Report: security_audit/trivy-image/trivy-image-report.json"
        } else {
            Write-Output "[FAIL] Trivy Image scan exited with code $ExitCode"
            $output | Out-File -FilePath $ErrorFile -Encoding utf8
        }
    } catch {
        Write-Output "[FAIL] Trivy Image scan failed: $_"
        $_ | Out-String | Out-File -FilePath $ErrorFile -Encoding utf8
    }
}

# Scanner Definitions Hashtable
$Scans = @{
    "checkov"      = @{ Name = "checkov"; DisplayName = "Checkov (IaC & Dockerfile)"; Block = $CheckovBlock }
    "gitleaks"     = @{ Name = "gitleaks"; DisplayName = "Gitleaks (Secrets detection)"; Block = $GitleaksBlock }
    "osv-scanner"  = @{ Name = "osv-scanner"; DisplayName = "OSV-Scanner (Open-source vulnerabilities)"; Block = $OsvBlock }
    "semgrep"      = @{ Name = "semgrep"; DisplayName = "Semgrep (Static Analysis SAST)"; Block = $SemgrepBlock }
    "trivy"        = @{ Name = "trivy"; DisplayName = "Trivy Code (Vulnerabilities & misconfigurations)"; Block = $TrivyBlock }
    "trufflehog"   = @{ Name = "trufflehog"; DisplayName = "TruffleHog (Secrets & high-entropy credentials)"; Block = $TrufflehogBlock }
    "trivy-image"  = @{ Name = "trivy-image"; DisplayName = "Trivy Image (Local Docker Image Scan)"; Block = $TrivyImageBlock }
}

# 3. Prompt for Scanner selection
Write-Host "`nAvailable security scans:" -ForegroundColor Cyan
Write-Host "1) All Scans (Default, runs in parallel)"
Write-Host "2) Checkov (IaC & Dockerfile)"
Write-Host "3) Gitleaks (Secrets detection)"
Write-Host "4) OSV-Scanner (Open-source vulnerability scanner)"
Write-Host "5) Semgrep (Static Analysis SAST)"
Write-Host "6) Trivy Code (Vulnerabilities & misconfigurations)"
Write-Host "7) TruffleHog (Secrets & high-entropy credentials)"
Write-Host "8) Trivy Image (Local Docker Image Scan)"

$ScanChoice = Read-Host "Select which scan(s) to run (comma-separated, e.g., 2,3,5 or 1 for all)"
if ([string]::IsNullOrWhiteSpace($ScanChoice)) {
    $ScanChoice = "1"
}

$SelectedScans = @()
$PromptForImage = $false

if ($ScanChoice -contains "1" -or $ScanChoice.Contains("1")) {
    $SelectedScans += $Scans["checkov"]
    $SelectedScans += $Scans["gitleaks"]
    $SelectedScans += $Scans["osv-scanner"]
    $SelectedScans += $Scans["semgrep"]
    $SelectedScans += $Scans["trivy"]
    $SelectedScans += $Scans["trufflehog"]
    $PromptForImage = $true
} else {
    if ($ScanChoice.Contains("2")) { $SelectedScans += $Scans["checkov"] }
    if ($ScanChoice.Contains("3")) { $SelectedScans += $Scans["gitleaks"] }
    if ($ScanChoice.Contains("4")) { $SelectedScans += $Scans["osv-scanner"] }
    if ($ScanChoice.Contains("5")) { $SelectedScans += $Scans["semgrep"] }
    if ($ScanChoice.Contains("6")) { $SelectedScans += $Scans["trivy"] }
    if ($ScanChoice.Contains("7")) { $SelectedScans += $Scans["trufflehog"] }
    if ($ScanChoice.Contains("8")) {
        $SelectedScans += $Scans["trivy-image"]
        $PromptForImage = $true
    }
}

# Prompt for Image Tag if required
$ImageTag = ""
if ($PromptForImage) {
    $ImageTag = Read-Host "Enter the local Docker image tag to scan (e.g. docker.io/sherlockparadox/vindicator). Leave empty to skip image scan"
    if ($ScanChoice.Contains("8") -and [string]::IsNullOrWhiteSpace($ImageTag)) {
        Write-Host "Error: Local Docker Image Scan was explicitly selected but no image tag was provided." -ForegroundColor Red
        Exit 1
    }
    # If choice is 'All Scans' and they provided an image tag, include it in the run list
    if (($ScanChoice -contains "1" -or $ScanChoice.Contains("1")) -and -not [string]::IsNullOrWhiteSpace($ImageTag)) {
        $SelectedScans += $Scans["trivy-image"]
    }
}

# Decide Parallel vs Sequential
$RunInParallel = $false
if ($ScanChoice -contains "1" -or $ScanChoice.Contains("1") -or $SelectedScans.Count -gt 1) {
    $RunInParallel = $true
}

if ($RunInParallel) {
    Write-Host "`n====================================================" -ForegroundColor Cyan
    Write-Host "Running selected scans in PARALLEL..." -ForegroundColor Cyan
    Write-Host "====================================================" -ForegroundColor Cyan
    
    $Jobs = @()
    foreach ($Scan in $SelectedScans) {
        Write-Host "Starting job for: $($Scan.DisplayName)" -ForegroundColor Yellow
        # Start background job, passing all required context parameters
        $Job = Start-Job -Name $Scan.Name -ScriptBlock $Scan.Block -ArgumentList $Engine, $MountPath, $AuditDir, $ErrorDir, $ImageTag
        $Jobs += $Job
    }
    
    Write-Host "`nWaiting for all background scan jobs to finish..." -ForegroundColor Yellow
    
    $JobStatus = @{}
    foreach ($Job in $Jobs) {
        $JobStatus[$Job.Name] = "Running"
    }
    
    while ($true) {
        $AllDone = $true
        foreach ($Job in $Jobs) {
            $CurrentJob = Get-Job -Id $Job.Id
            $State = $CurrentJob.State
            if ($State -eq "Running") {
                $AllDone = $false
            } else {
                if ($JobStatus[$Job.Name] -eq "Running") {
                    $JobStatus[$Job.Name] = $State
                    Write-Host "`n----------------------------------------------------" -ForegroundColor Cyan
                    Write-Host "[+] Job Finished: $($Job.Name) (State: $State)" -ForegroundColor Green
                    Write-Host "----------------------------------------------------" -ForegroundColor Cyan
                    $JobOutput = Receive-Job -Id $Job.Id
                    if ($JobOutput) {
                        Write-Output $JobOutput
                    }
                }
            }
        }
        if ($AllDone) { break }
        Start-Sleep -Seconds 2
    }
    
    # Clean up job resources
    Remove-Job -Job $Jobs -Force
} else {
    Write-Host "`n====================================================" -ForegroundColor Cyan
    Write-Host "Running selected scan(s) SEQUENTIALLY..." -ForegroundColor Cyan
    Write-Host "====================================================" -ForegroundColor Cyan
    
    foreach ($Scan in $SelectedScans) {
        Write-Host "`n----------------------------------------------------" -ForegroundColor Cyan
        Write-Host "[+] Starting: $($Scan.DisplayName)" -ForegroundColor Cyan
        Write-Host "----------------------------------------------------" -ForegroundColor Cyan
        
        & $Scan.Block $Engine $MountPath $AuditDir $ErrorDir $ImageTag
    }
}

Write-Host "`n====================================================" -ForegroundColor Cyan
Write-Host "All requested scans are completed!" -ForegroundColor Green
Write-Host "Audit reports saved in: $AuditDir" -ForegroundColor Green
Write-Host "------------------- Report Files -------------------" -ForegroundColor Yellow
Get-ChildItem -Path $AuditDir -Recurse | Where-Object { -not $_.PSIsContainer } | Select-Object Name, Length, LastWriteTime

if (Test-Path $ErrorDir) {
    $ErrorFiles = Get-ChildItem -Path $ErrorDir
    if ($ErrorFiles.Count -gt 0) {
        Write-Host "`n[!] Some scans encountered errors. Details logged in:" -ForegroundColor Red
        foreach ($ErrFile in $ErrorFiles) {
            Write-Host "  - $($ErrFile.FullName)" -ForegroundColor Red
        }
    }
}

# 4. Optional: Upload to Vindicator Security Control Plane Backend
if ($env:VINDICATOR_CONTROL_PLANE_URL) {
    Write-Host "`n[+] Ingesting scan reports to Vindicator Control Plane ($env:VINDICATOR_CONTROL_PLANE_URL)..." -ForegroundColor Cyan
    $Reports = Get-ChildItem -Path $AuditDir -Recurse -Filter "*.json" | Where-Object { -not $_.PSIsContainer }
    foreach ($Report in $Reports) {
        try {
            $Content = Get-Content -Raw -Path $Report.FullName
            $ScannerType = $Report.Directory.Name.ToUpper()
            $Payload = @{
                asset = (Split-Path $TargetDir -Leaf)
                scanner = $ScannerType
                rawReportJson = $Content
            } | ConvertTo-Json -Depth 10 -Compress
            Invoke-RestMethod -Uri "$env:VINDICATOR_CONTROL_PLANE_URL/api/v1/scans/ingest" -Method Post -Body $Payload -ContentType "application/json" -TimeoutSec 30
            Write-Host "  [OK] Uploaded $($Report.Name) ($ScannerType)" -ForegroundColor Green
        } catch {
            Write-Host "  [WARN] Failed to upload $($Report.Name): $_" -ForegroundColor Yellow
        }
    }
}

Write-Host "====================================================" -ForegroundColor Cyan

