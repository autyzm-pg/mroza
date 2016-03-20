python generator.py
adb shell < ./scripts/permissions.bat
adb push gen.txt /data/data/mroza.forms/databases
adb shell < ./scripts/fill_db.bat

