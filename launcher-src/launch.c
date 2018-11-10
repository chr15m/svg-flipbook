#define NOMINMAX
#define UNICODE
#include <windows.h>
#include <stdio.h>
#include <direct.h>

int main(void)
{
    /*HMODULE hModule = GetModuleHandleW(NULL);
    WCHAR path[MAX_PATH];
    GetModuleFileNameW(hModule, path, MAX_PATH);*/

    //wchar_t buffer[1024] = L"";
    /*wsprintf(buffer, L"%s", path); // a CAPITAL S
    MessageBox (0, buffer , buffer , 0) ;

    char p[FILENAME_MAX];
    _getcwd(p, sizeof(p));
    wsprintf(buffer, L"%s", p); // a CAPITAL S
    MessageBox (0, buffer , buffer , 0) ;*/
    
    TCHAR p2[MAX_PATH+1] = L"";
    DWORD len = GetCurrentDirectory(MAX_PATH, p2);
    //wsprintf(buffer, L"lib\\ChromiumPortable.exe --allow-file-access-from-files --app=file:///%s\\lib\\index.html", p2); // a CAPITAL S
    //MessageBox (0, buffer, buffer, 0);

    //MessageBox(0, "Blah blah...", buffer, MB_SETFOREGROUND);

    char cmd[1024];
    sprintf(cmd, "lib\\ChromiumPortable.exe --allow-file-access-from-files --app=file:///%S\\lib\\index.html", p2);
    //system((char *)temp);

    //wsprintf(buffer, L"%s", cmd);
    //MessageBox (0, buffer, buffer, 0);

    // MessageBox( 0, "Blah blah...", "My Windows app!", MB_SETFOREGROUND );
    //return system("cmd /c START /MIN lib\\ChromiumPortable.exe --allow-file-access-from-files --app=file:///%~dp0lib\\index.html");
    //return system("lib\\ChromiumPortable.exe --allow-file-access-from-files --app=file:///%~dp0lib\\index.html");
    //ShellExecute("lib\\ChromiumPortable.exe --allow-file-access-from-files --app=file:///%~dp0lib\\index.html");
    //return WinExec("lib\\ChromiumPortable.exe --allow-file-access-from-files --app=file:///%~dp0lib\\index.html", 0);
    //return WinExec((char*)buffer, 0);
    return WinExec(cmd, 0);
}

/* #include<stdio.h>
int main()
{
  int a,b,c;
  printf("Enter two numbers:\n");
  scanf("%d%d",&a,&b);
  c=a+b;
  printf("Sum of two numbers %d",c);
  getch();
  return 0;
}*/
