param(
    [string]$MysqlExe = 'C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe',
    [string]$HostName = '42.193.104.179',
    [string]$Database = 'luntan'
)
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$profile = Get-Content (Join-Path $root 'hope-api/src/main/resources/application-prod.yml') -Raw
$username = [Environment]::GetEnvironmentVariable('DB_USERNAME')
$password = [Environment]::GetEnvironmentVariable('DB_PASSWORD')
if (-not $username) { $username = [regex]::Match($profile, 'username:\s*''\$\{DB_USERNAME:([^}]*)\}''').Groups[1].Value }
if (-not $password) { $password = [regex]::Match($profile, 'password:\s*''\$\{DB_PASSWORD:([^}]*)\}''').Groups[1].Value }
if (-not $username -or -not $password) { throw '无法取得生产数据库账号' }
$options = Join-Path $root ('ironbox-mysql-' + [guid]::NewGuid().ToString('N') + '.cnf')
try {
    [IO.File]::WriteAllText($options, "[client]`nhost=$HostName`nport=3306`nuser=$username`npassword=$password`ndefault-character-set=utf8mb4`n", [Text.UTF8Encoding]::new($false))
    Get-Content (Join-Path $root 'hope-api/src/main/resources/sql/ironbox_schema.sql') -Raw |
        & $MysqlExe "--defaults-extra-file=$options" $Database
    if ($LASTEXITCODE -ne 0) { throw 'AI铁盒数据表迁移失败' }
    & $MysqlExe "--defaults-extra-file=$options" $Database -N -e "SHOW TABLES LIKE 'ironbox_%'"
    if ($LASTEXITCODE -ne 0) { throw 'AI铁盒数据表校验失败' }
} finally {
    Remove-Item -LiteralPath $options -Force -ErrorAction SilentlyContinue
}
