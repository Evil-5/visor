import re

with open('security_scan.ps1', 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    new_lines.append(line)
    
    # Inject RUNNING
    if 'Write-Output "[+] Starting' in line:
        new_lines.append('    Report-Status $ScanName "RUNNING"\n')
        
    # Inject COMPLETED
    if 'Write-Output "[OK]' in line:
        new_lines.append('            Report-Status $ScanName "COMPLETED"\n')
        
    # Inject FAILED (else block)
    if 'Write-Output "[FAIL]' in line:
        # Check if we are inside a catch or else. The indentation usually is around 12 spaces or 8 spaces.
        # Just use generic indentation
        new_lines.append('            Report-Status $ScanName "FAILED"\n')

with open('security_scan.ps1', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("Injected status reporting into security_scan.ps1")
