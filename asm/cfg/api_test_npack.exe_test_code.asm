0x00404290:	cmpl 0x404e4c, $0x0<UINT8>
0x00404297:	jne 5
0x00404299:	jmp 0x0040429f
0x0040429f:	call 0x004042ea
0x004042ea:	pushl %esi
0x004042eb:	pushl %edi
0x004042ec:	pushl $0x404180<UINT32>
0x004042f1:	call LoadLibraryA@kernel32.dll
LoadLibraryA@kernel32.dll: API Node	
0x004042f7:	movl %esi, 0x404c08
0x004042fd:	movl %edi, %eax
0x004042ff:	pushl $0x404190<UINT32>
0x00404304:	pushl %edi
0x00404305:	call GetProcAddress@kernel32.dll
GetProcAddress@kernel32.dll: API Node	
0x00404307:	pushl $0x4041a0<UINT32>
0x0040430c:	pushl %edi
0x0040430d:	movl 0x404e48, %eax
0x00404312:	call GetProcAddress@kernel32.dll
0x00404314:	popl %edi
0x00404315:	movl 0x404e44, %eax
0x0040431a:	popl %esi
0x0040431b:	ret

0x004042a4:	call 0x0040431c
0x0040431c:	pushl %esi
0x0040431d:	pushl %edi
0x0040431e:	pushl $0x4<UINT8>
0x00404320:	pushl $0x3000<UINT32>
0x00404325:	pushl $0x24<UINT8>
0x00404327:	pushl $0x0<UINT8>
0x00404329:	call VirtualAlloc@kernel32.dll
VirtualAlloc@kernel32.dll: API Node	
0x0040432f:	movl %esi, %eax
0x00404331:	movl %edi, $0x404e00<UINT32>
0x00404336:	pushl %esi
0x00404337:	pushl %edi
0x00404338:	call 0x00404160
0x00404160:	movl %eax, 0x8(%esp)
0x00404164:	movl %ecx, 0x4(%esp)
0x00404168:	pushl %eax
0x00404169:	pushl %ecx
0x0040416a:	call 0x00404b30
0x00404b30:	pusha
0x00404b31:	movl %esi, 0x24(%esp)
0x00404b35:	movl %edi, 0x28(%esp)
0x00404b39:	cld
0x00404b3a:	movb %dl, $0xffffff80<UINT8>
0x00404b3c:	xorl %ebx, %ebx
0x00404b3e:	movsb %es:(%edi), %ds:(%esi)
0x00404b3f:	movb %bl, $0x2<UINT8>
0x00404b41:	call 0x00404bb3
0x00404bb3:	addb %dl, %dl
0x00404bb5:	jne 0x00404bbc
0x00404bb7:	movb %dl, (%esi)
0x00404bb9:	incl %esi
0x00404bba:	adcb %dl, %dl
0x00404bbc:	ret

0x00404b46:	jae 0x00404b3e
0x00404b48:	xorl %ecx, %ecx
0x00404b4a:	call 0x00404bb3
0x00404b4f:	jae 0x00404b6d
0x00404b51:	xorl %eax, %eax
0x00404b53:	call 0x00404bb3
0x00404b58:	jae 0x00404b7d
0x00404b5a:	movb %bl, $0x2<UINT8>
0x00404b5c:	incl %ecx
0x00404b5d:	movb %al, $0x10<UINT8>
0x00404b5f:	call 0x00404bb3
0x00404b64:	adcb %al, %al
0x00404b66:	jae 0x00404b5f
0x00404b68:	jne 63
0x00404b6a:	stosb %es:(%edi), %al
0x00404b6b:	jmp 0x00404b41
0x00404b7d:	lodsb %al, %ds:(%esi)
0x00404b7e:	shrl %eax
0x00404b80:	je 0x00404bcf
0x00404b82:	adcl %ecx, %ecx
0x00404b84:	jmp 0x00404ba2
0x00404ba2:	incl %ecx
0x00404ba3:	incl %ecx
0x00404ba4:	xchgl %ebp, %eax
0x00404ba5:	movl %eax, %ebp
0x00404ba7:	movb %bl, $0x1<UINT8>
0x00404ba9:	pushl %esi
0x00404baa:	movl %esi, %edi
0x00404bac:	subl %esi, %eax
0x00404bae:	rep movsb %es:(%edi), %ds:(%esi)
0x00404bb0:	popl %esi
0x00404bb1:	jmp 0x00404b41
0x00404b6d:	call 0x00404bbf
0x00404bbf:	incl %ecx
0x00404bc0:	call 0x00404bb3
0x00404bc5:	adcl %ecx, %ecx
0x00404bc7:	call 0x00404bb3
0x00404bcc:	jb 0x00404bc0
0x00404bce:	ret

0x00404b72:	subl %ecx, %ebx
0x00404b74:	jne 0x00404b86
0x00404b76:	call 0x00404bbd
0x00404bbd:	xorl %ecx, %ecx
0x00404b7b:	jmp 0x00404ba5
0x00404b86:	xchgl %ecx, %eax
0x00404b87:	decl %eax
0x00404b88:	shll %eax, $0x8<UINT8>
0x00404b8b:	lodsb %al, %ds:(%esi)
0x00404b8c:	call 0x00404bbd
0x00404b91:	cmpl %eax, $0x7d00<UINT32>
0x00404b96:	jae 10
0x00404b98:	cmpb %ah, $0x5<UINT8>
0x00404b9b:	jae 6
0x00404b9d:	cmpl %eax, $0x7f<UINT8>
0x00404ba0:	ja 2
0x00404bcf:	subl %edi, 0x28(%esp)
0x00404bd3:	movl 0x1c(%esp), %edi
0x00404bd7:	popa
0x00404bd8:	ret

0x0040416f:	addl %esp, $0x8<UINT8>
0x00404172:	ret

0x0040433d:	pushl $0x24<UINT8>
0x0040433f:	pushl %esi
0x00404340:	pushl %edi
0x00404341:	call 0x0040403a
0x0040403a:	pushl %ebp
0x0040403b:	movl %ebp, %esp
0x0040403d:	pushl %esi
0x0040403e:	pushl %edi
0x0040403f:	movl %ecx, 0x10(%ebp)
0x00404042:	movl %esi, 0xc(%ebp)
0x00404045:	movl %edi, 0x8(%ebp)
0x00404048:	rep movsb %es:(%edi), %ds:(%esi)
0x0040404a:	popl %edi
0x0040404b:	popl %esi
0x0040404c:	popl %ebp
0x0040404d:	ret

0x00404346:	addl %esp, $0x14<UINT8>
0x00404349:	pushl $0x4000<UINT32>
0x0040434e:	pushl $0x0<UINT8>
0x00404350:	pushl %esi
0x00404351:	call VirtualFree@kernel32.dll
VirtualFree@kernel32.dll: API Node	
0x00404357:	popl %edi
0x00404358:	popl %esi
0x00404359:	ret

0x004042a9:	movl %eax, $0x404290<UINT32>
0x004042ae:	subl %eax, 0x404e08
0x004042b4:	movl 0x404e40, %eax
0x004042b9:	call 0x0040435a
0x0040435a:	subl %esp, $0x10<UINT8>
0x0040435d:	pushl %ebp
0x0040435e:	pushl %esi
0x0040435f:	pushl %edi
0x00404360:	xorl %ebp, %ebp
0x00404362:	pushl 0x404e40
0x00404368:	movl 0x18(%esp), %ebp
0x0040436c:	call 0x00404030
0x00404030:	movl %ecx, 0x4(%esp)
0x00404034:	movl %eax, 0x3c(%ecx)
0x00404037:	addl %eax, %ecx
0x00404039:	ret

0x00404371:	movl %edi, %eax
0x00404373:	popl %ecx
0x00404374:	movl 0x18(%esp), %edi
0x00404378:	movl 0x10(%esp), %ebp
0x0040437c:	movzwl %eax, 0x14(%edi)
0x00404380:	leal %esi, 0x18(%eax,%edi)
0x00404384:	movzwl %eax, 0x6(%edi)
0x00404388:	decl %eax
0x00404389:	testl %eax, %eax
0x0040438b:	jle 371
0x00404391:	pushl %ebx
0x00404392:	cmpl 0x10(%esi), %ebp
0x00404395:	movl 0x10(%esp), %ebp
0x00404399:	je 0x004044ed
0x0040439f:	cmpl 0x14(%esi), %ebp
0x004044ed:	addl %esi, $0x28<UINT8>
0x004044f0:	movzwl %eax, 0x6(%edi)
0x004044f4:	incl 0x14(%esp)
0x004044f8:	decl %eax
0x004044f9:	cmpl 0x14(%esp), %eax
0x004044fd:	jl 0x00404392
