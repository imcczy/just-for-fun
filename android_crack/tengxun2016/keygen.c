//
//  main.c
//  Test
//
//  Created by imcczy on 16/8/23.
//  Copyright © 2016年 imcczy. All rights reserved.
//

#include <string.h>
#include <stdio.h>
#include <stdlib.h>

static char base64_index[64] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

void nameCrypt(char const *name,int *namec){
    char name_proc[20] = {'\0'};
    int len =(int)strlen(name),temp=0;
    for (int i = 0; i < 16; ++i) {
       *(int *)(name_proc+i) = (int)(len * name[i % len] * (i + 0x1339e7e)+temp);
        temp = *(int *) (name_proc+1+i);
    }
    for (int i=0; i<5; i++) {
        namec[i] = *(int *)(name_proc+i*4) / 10;
    }
}


void keygen(char *code,char *key){
    for (int i =0; i<=6; i++) {
        char temp = 0;
        if (i == 6) {
            temp = (*(code + i * 3) & 0xff) >> 2;
            key[i*4] = base64_index[temp];
            temp = ((*(code + i * 3) & 0x03) << 4) | ((*(code + i * 3 + 1) & 0xff) >> 4);
            key[i*4+1] = base64_index[temp];
            temp = ((*(code + i * 3 + 1) & 0x0f) << 2) & 0x3f;
             key[i*4+2] = base64_index[temp];
        }
        else{
            temp = (*(code + i * 3) & 0xff) >> 2;
            key[i*4] = base64_index[temp];
            temp = ((*(code + i * 3) & 0x03) << 4) | ((*(code + i * 3 + 1) & 0xff) >> 4);
            key[i*4+1] = base64_index[temp];
            temp = ((*(code + i * 3 + 1) & 0x0f) << 2) | ((*(code + i * 3 + 2) & 0xff) >> 6);
            key[i*4+2] = base64_index[temp];
            temp = (*(code + i * 3 + 2) & 0x3f);
            key[i*4+3] = base64_index[temp];
        }
    }
}


int main(){
    int namecrypt[5],codecrypt[5];
    char *hashcode;
    char key[27];
    const char name[]= "imcczy";
    
    nameCrypt(name,namecrypt);
    
    codecrypt[3] = namecrypt[2] + namecrypt[3];
    codecrypt[0] = codecrypt[3] + namecrypt[2];
    codecrypt[4] = namecrypt[0] + namecrypt[1];
    codecrypt[2] = codecrypt[4] + namecrypt[0];
    codecrypt[1] = 3*namecrypt[2] - namecrypt[4];
    hashcode = (char *)codecrypt;
    keygen(hashcode, key);
    
    printf("%s\n",key);
    
    return 0;
}
