0x00400154:	xchgl 0x4052d4, %esp
0x0040015a:	popa
0x0040015b:	xchgl %esp, %eax
0x0040015c:	pushl %ebp
0x0040015d:	movsb %es:(%edi), %ds:(%esi)
0x0040015e:	movb %dh, $0xffffff80<UINT8>
0x00400160:	call 0x004001e8
0x004001e8:	addb %dl, %dl
0x004001ea:	jne 0x004001f1
0x004001ec:	movb %dl, (%esi)
0x004001ee:	incl %esi
0x004001ef:	adcb %dl, %dl
0x004001f1:	ret

0x00400162:	jae 0x0040015d
0x00400164:	xorl %ecx, %ecx
0x00400166:	call 0x004001e8
0x00400168:	jae 0x00400180
0x0040016a:	xorl %eax, %eax
0x0040016c:	call 0x004001e8
0x0040016e:	jae 0x0040018f
0x00400170:	movb %dh, $0xffffff80<UINT8>
0x00400172:	incl %ecx
0x00400173:	movb %al, $0x10<UINT8>
0x00400175:	call 0x004001e8
0x00400177:	adcb %al, %al
0x00400179:	jae 0x00400175
0x0040017b:	jne 0x004001b7
0x0040017d:	stosb %es:(%edi), %al
0x0040017e:	jmp 0x00400160
0x004001b7:	pushl %esi
0x004001b8:	movl %esi, %edi
0x004001ba:	subl %esi, %eax
0x004001bc:	rep movsb %es:(%edi), %ds:(%esi)
0x004001be:	popl %esi
0x004001bf:	jmp 0x00400160
0x0040018f:	lodsb %al, %ds:(%esi)
0x00400190:	shrl %eax
0x00400192:	je 0x004001c1
0x00400194:	adcl %ecx, %ecx
0x00400196:	jmp 0x004001b0
0x004001b0:	incl %ecx
0x004001b1:	incl %ecx
0x004001b2:	xchgl %ebp, %eax
0x004001b3:	movl %eax, %ebp
0x004001b5:	movb %dh, $0x0<UINT8>
0x00400180:	call 0x004001de
0x004001de:	incl %ecx
0x004001df:	call 0x004001e8
0x004001e1:	adcl %ecx, %ecx
0x004001e3:	call 0x004001e8
0x004001e5:	jb 0x004001df
0x004001e7:	ret

0x00400183:	addb %dh, %dh
0x00400185:	sbbl %ecx, $0x1<UINT8>
0x00400188:	jne 0x00400198
0x0040018a:	call 0x004001dc
0x004001dc:	xorl %ecx, %ecx
0x0040018d:	jmp 0x004001b3
0x00400198:	xchgl %ecx, %eax
0x00400199:	decl %eax
0x0040019a:	shll %eax, $0x8<UINT8>
0x0040019d:	lodsb %al, %ds:(%esi)
0x0040019e:	call 0x004001dc
0x004001a1:	cmpl %eax, -8(%ebx)
0x004001a4:	jae 10
0x004001a6:	cmpb %ah, $0x5<UINT8>
0x004001a9:	jae 0x004001b1
0x004001ab:	cmpl %eax, $0x7f<UINT8>
0x004001ae:	ja 0x004001b2
0x004001c1:	popl %esi
0x004001c2:	lodsl %eax, %ds:(%esi)
0x004001c3:	xchgl %edi, %eax
0x004001c4:	lodsl %eax, %ds:(%esi)
0x004001c5:	pushl %eax
0x004001c6:	call LoadLibraryA@kernel32.dll
LoadLibraryA@kernel32.dll: API Node	
0x004001c9:	xchgl %ebp, %eax
0x004001ca:	movl %eax, (%edi)
0x004001cc:	incl %eax
0x004001cd:	js 0x004001c2
0x004001cf:	jne 0x004001d4
0x004001d4:	pushl %eax
0x004001d5:	pushl %ebp
0x004001d6:	call GetProcAddress@kernel32.dll
GetProcAddress@kernel32.dll: API Node	
0x004001d9:	stosl %es:(%edi), %eax
0x004001da:	jmp 0x004001ca
0x004001d1:	jmp 0x00401000
0x00401000:	pushl %eax
0x00401001:	pushl %ebx
0x00401002:	leal %eax, 0x403098
0x00401008:	pushl %eax
0x00401009:	call 0x004011f2
0x004011f2:	jmp 0x00000000
Unknown Node: Unknown Node	
