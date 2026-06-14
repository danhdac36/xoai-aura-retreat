$sqlPath = "d:\SWP301\su26-swp391-se2023-g6\03_Design\SQL\UC16_UC19_seed_150_menu_items.sql"
$jsPath = "d:\SWP301\su26-swp391-se2023-g6\auramoon\src\main\resources\static\js\fnb\menu-data.js"

$content = Get-Content -Raw -Encoding utf8 $sqlPath
$null = $content -match "INSERT INTO MENU_ITEM [^)]*\)[\s]*VALUES([\s\S]*?);"
$valuesStr = $Matches[1]

$regex = "(?i)\(\s*N'([^']*)'\s*,\s*([\d.]+)\s*,\s*N'([^']*)'\s*,\s*(\d)\s*\)"
$matches = [regex]::Matches($valuesStr, $regex)

$menuItems = @()
$idx = 1
foreach ($m in $matches) {
    $item = [PSCustomObject]@{
        id = $idx
        itemName = $m.Groups[1].Value
        price = [double]$m.Groups[2].Value
        ingredient = $m.Groups[3].Value
        isAvailable = ($m.Groups[4].Value -eq '1')
    }
    $menuItems += $item
    $idx++
}

$jsContent = "const menuData = " + (ConvertTo-Json -InputObject $menuItems -Depth 10) + ";"
Set-Content -Path $jsPath -Value $jsContent -Encoding utf8
Write-Output "Successfully parsed $($menuItems.Count) items and wrote to $jsPath"
