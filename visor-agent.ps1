# Vindicator VISOR Agent
# Polls the Control Plane for pending Scan Jobs and executes them securely on the local host.

$ErrorActionPreference = "Stop"

$ControlPlaneUrl = "http://localhost:8080"
if ($env:VINDICATOR_CONTROL_PLANE_URL) {
    $ControlPlaneUrl = $env:VINDICATOR_CONTROL_PLANE_URL
}

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "       Vindicator VISOR Polling Agent               " -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "Control Plane: $ControlPlaneUrl" -ForegroundColor Green
Write-Host "Waiting for jobs..." -ForegroundColor Yellow

while ($true) {
    try {
        # Poll for pending jobs
        $Response = Invoke-RestMethod -Uri "$ControlPlaneUrl/api/v1/scans/jobs/pending" -Method Get -TimeoutSec 5 -ErrorAction SilentlyContinue
        
        if ($Response -and $Response.jobId) {
            Write-Host "`n[+] Picked up new job: $($Response.jobId)" -ForegroundColor Cyan
            Write-Host "Target Directory: $($Response.targetDir)" -ForegroundColor Cyan
            
            # Execute the local scanner script
            $env:VINDICATOR_CONTROL_PLANE_URL = $ControlPlaneUrl
            & "$PSScriptRoot\security_scan.ps1" -TargetDir $Response.targetDir -JobId $Response.jobId
            
            Write-Host "`n[+] Job $($Response.jobId) completed. Waiting for next job..." -ForegroundColor Yellow
        }
    } catch {
        # Backend might be down, ignore and keep polling
    }
    
    Start-Sleep -Seconds 5
}
