#define NOMINMAX
#define UNICODE
#include <windows.h>
#include <stdio.h>
#include <shlobj.h>

int main(void)
{
    TCHAR cwd[MAX_PATH+1] = L"";
    DWORD len = GetCurrentDirectory(MAX_PATH, cwd);

    TCHAR homedir[MAX_PATH];
    SHGetFolderPath(NULL, CSIDL_PERSONAL | CSIDL_FLAG_CREATE, NULL, 0, homedir);

    char cmd[4096];
    sprintf(cmd, "lib\\ChromiumPortable.exe --user-data-dir=\"%S\\svg-animation-assistant-chrome\" --window-size=600,600 --allow-file-access-from-files --app=\"file:///%S\\lib\\index.html\"", homedir, cwd);

    return WinExec(cmd, 0);
}
