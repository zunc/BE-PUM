0x005f0000:	pushl %esi
0x005f0001:	pushl %eax
0x005f0002:	pushl %ebx
0x005f0003:	call 0x005f0009
0x005f0009:	popl %eax
0x005f000a:	movl %ebx, %eax
0x005f000c:	incl %eax
0x005f000d:	subl %eax, $0x1ea000<UINT32>
0x005f0012:	subl %eax, $0x100c6b00<UINT32>
0x005f0017:	addl %eax, $0x100c6af7<UINT32>
0x005f001c:	cmpb (%ebx), $0xffffffcc<UINT8>
0x005f001f:	jne 25
0x005f0021:	movb (%ebx), $0x0<UINT8>
0x005f0024:	movl %ebx, $0x1000<UINT32>
0x005f0029:	pushl $0x38e28a12<UINT32>
0x005f002e:	pushl $0x13b18669<UINT32>
0x005f0033:	pushl %ebx
0x005f0034:	pushl %eax
0x005f0035:	call 0x005f0044
0x005f0044:	pushl %ebp
0x005f0045:	movl %ebp, %esp
0x005f0047:	pushl %eax
0x005f0048:	pushl %ebx
0x005f0049:	pushl %ecx
0x005f004a:	pushl %esi
0x005f004b:	movl %esi, 0x8(%ebp)
0x005f004e:	movl %ecx, 0xc(%ebp)
0x005f0051:	shrl %ecx, $0x2<UINT8>
0x005f0054:	movl %eax, 0x10(%ebp)
0x005f0057:	movl %ebx, 0x14(%ebp)
0x005f005a:	testl %ecx, %ecx
0x005f005c:	je 0x005f0068
0x005f005e:	xorl (%esi), %eax
0x005f0060:	addl (%esi), %ebx
0x005f0062:	addl %esi, $0x4<UINT8>
0x005f0065:	decl %ecx
0x005f0066:	jmp 0x005f005a
0x005f0068:	popl %esi
0x005f0069:	popl %ecx
0x005f006a:	popl %ebx
0x005f006b:	popl %eax
0x005f006c:	leave
0x005f006d:	ret $0x10<UINT16>

0x005f003a:	addl %eax, $0x14<UINT8>
0x005f003d:	movl 0x8(%esp), %eax
0x005f0041:	popl %ebx
0x005f0042:	popl %eax
0x005f0043:	ret

0x00406014:	jmp 0x00407751
0x00407751:	movl %eax, %ebp
0x00407753:	movl %edx, %esp
0x00407755:	pusha
0x00407756:	call 0x0040775b
0x0040775b:	popl %ebp
0x0040775c:	subl %ebp, $0x112b1747<UINT32>
0x00407762:	movl 0x112b15a9(%ebp), %edx
0x00407768:	movl 0x112b09c5(%ebp), %esi
0x0040776e:	movl 0x112b09e5(%ebp), %eax
0x00407774:	movl 0x112b1679(%ebp), %ebx
0x0040777a:	movl 0x112b1189(%ebp), %edi
0x00407780:	movl %eax, $0x0<UINT32>
0x00407785:	testl %eax, %eax
0x00407787:	je 0x00407796
0x00407796:	movl %eax, 0x24(%esp)
0x0040779a:	cmpl 0x112b1515(%ebp), $0x0<UINT8>
0x004077a1:	je 0x004077a9
0x004077a9:	cmpl 0x112b1191(%ebp), $0x0<UINT8>
0x004077b0:	je 0x004077be
0x004077be:	pushl $0x45<UINT8>
0x004077c0:	call 0x00407868
0x00407868:	pushl %ebp
0x00407869:	movl %ebp, %esp
0x0040786b:	subl %esp, $0x28<UINT8>
0x0040786e:	pusha
0x0040786f:	call 0x00407874
0x00407874:	popl %edx
0x00407875:	subl %edx, $0x112b1860<UINT32>
0x0040787b:	movl %ebx, %edx
0x0040787d:	movl -4(%ebp), $0x0<UINT32>
0x00407884:	movl %eax, -4(%ebp)
0x00407887:	incl %eax
0x00407888:	movl -4(%ebp), %eax
0x0040788b:	cmpl -4(%ebp), $0x80<UINT32>
0x00407892:	je 0x004078a3
0x00407894:	movl %eax, 0x8(%ebp)
0x00407897:	movl 0x112b17ca(%ebx), %eax
0x0040789d:	incl 0x8(%ebp)
0x004078a0:	incl %ebx
0x004078a1:	jmp 0x00407884
0x004078a3:	movl -8(%ebp), %eax
0x004078a6:	popa
0x004078a7:	movl %eax, -8(%ebp)
0x004078aa:	leave
0x004078ab:	ret $0x4<UINT16>

0x004077c5:	pushl $0x783749a<UINT32>
0x004077ca:	call 0x004078ae
0x004078ae:	pushl %ebp
0x004078af:	movl %ebp, %esp
0x004078b1:	subl %esp, $0x84<UINT32>
0x004078b7:	pusha
0x004078b8:	call 0x004078bd
0x004078bd:	popl %edx
0x004078be:	subl %edx, $0x112b18a9<UINT32>
0x004078c4:	leal %eax, -132(%ebp)
0x004078ca:	movl %ebx, 0x8(%ebp)
0x004078cd:	movl -4(%ebp), $0x0<UINT32>
0x004078d4:	movl %ecx, -4(%ebp)
0x004078d7:	roll %ebx
0x004078d9:	movb (%eax), %bl
0x004078db:	incl %ecx
0x004078dc:	movl -4(%ebp), %ecx
0x004078df:	cmpl -4(%ebp), $0x80<UINT32>
0x004078e6:	jne 0x004078d4
0x004078e8:	movl -4(%ebp), $0x0<UINT32>
0x004078ef:	leal %edi, 0x112b17ca(%edx)
0x004078f5:	leal %esi, -132(%ebp)
0x004078fb:	movb %cl, (%esi)
0x004078fd:	movl %ebx, $0x1f4<UINT32>
0x00407902:	movl %eax, $0x785437ab<UINT32>
0x00407907:	rcll %eax, %cl
0x00407909:	movb %cl, (%edi)
0x0040790b:	rcll %eax, %cl
0x0040790d:	decl %ebx
0x0040790e:	jne 0x00407907
0x00407910:	imull %eax, %ebx
0x00407913:	incl %edi
0x00407914:	incl %esi
0x00407915:	movl %ecx, -4(%ebp)
0x00407918:	incl %ecx
0x00407919:	movl -4(%ebp), %ecx
0x0040791c:	cmpl %ecx, $0x80<UINT32>
0x00407922:	jne 0x004078fb
0x00407924:	popa
0x00407925:	leave
0x00407926:	ret $0x4<UINT16>

0x004077cf:	pushl $0xa894b25<UINT32>
0x004077d4:	call 0x004078ae
0x004077d9:	jmp 0x00407929
0x00407929:	movl %ecx, $0x7000<UINT32>
0x0040792e:	leal %edi, 0x112b1926(%ebp)
0x00407934:	decb (%edi)
0x00407936:	incl %edi
0x00407937:	decl %ecx
0x00407938:	jne 0x00407934
0x0040793a:	movl %eax, $0x48692121<UINT32>
0x0040793f:	movl %edx, $0xf0006000<UINT32>
0x00407944:	subl %edx, $0xf0000000<UINT32>
0x0040794a:	leal %eax, 0x112b1a7e(%ebp)
0x00407950:	pushl %eax
0x00407951:	pushl %fs:0
0x00407958:	movl %fs:0, %esp
0x0040795f:	call 0x00407964
0x00407964:	popl %eax
0x00407965:	andw %bx, $0xfffffd17<UINT16>
0x0040796a:	movl %ebx, $0x1d5221b<UINT32>
0x0040796f:	movb %bl, $0x34<UINT8>
0x00407971:	subl %eax, %edx
0x00407973:	movl %ebx, $0x1c3b49eb<UINT32>
0x00407978:	movl %ebx, $0x74d94725<UINT32>
0x0040797d:	andl %eax, $0xfffff000<UINT32>
0x00407982:	pusha
0x00407983:	movl %esi, %eax
0x00407985:	pushl %esi
0x00407986:	jnp 0
0x0040798c:	popl %esi
0x0040798d:	popa
0x0040798e:	andb %bl, $0x31<UINT8>
0x00407991:	movl %ecx, %edx
0x00407993:	xorl %edi, %edi
0x00407995:	cmpw (%eax), $0x5a4d<UINT16>
0x0040799a:	jne 0x004079aa
0x004079aa:	subl %eax, $0x1000<UINT32>
0x004079af:	incl %edi
0x004079b0:	jmp 0x00407995
0x0040799c:	movzwl %edx, 0x3c(%eax)
0x004079a0:	addl %edx, %eax
0x004079a2:	cmpl (%edx), $0x4550<UINT32>
0x004079a8:	je 0x004079b2
0x004079b2:	movl 0x112b10e9(%ebp), %eax
0x004079b8:	popl %fs:0
0x004079bf:	addl %esp, $0x4<UINT8>
0x004079c2:	leal %edx, 0x112b1a91(%ebp)
0x004079c8:	pushl %edx
0x004079c9:	pushl %fs:0
0x004079d0:	movl %fs:0, %esp
0x004079d7:	movl %edx, %eax
0x004079d9:	addl %eax, %ecx
0x004079db:	subl %eax, $0x1000<UINT32>
0x004079e0:	movl %esi, $0x1<UINT32>
0x004079e5:	testl %esi, %esi
0x004079e7:	je 44
0x004079e9:	movl %esi, $0x506d<UINT32>
0x004079ee:	testl %esi, %esi
0x004079f0:	jne 0x004079fd
0x004079fd:	addl %esi, %edx
0x004079ff:	movl %esi, 0x10(%esi)
0x00407a02:	addl %esi, %edx
0x00407a04:	movl %ecx, %esi
0x00407a06:	addl %ecx, $0x8<UINT8>
0x00407a09:	movl 0x112b0fb1(%ebp), %ecx
0x00407a0f:	movl %esi, (%esi)
0x00407a11:	movl %ecx, %esi
0x00407a13:	jmp 0x00407a21
0x00407a21:	andl %esi, $0xffff0000<UINT32>
0x00407a27:	cmpl %esi, $0x80000000<UINT32>
0x00407a2d:	jbe 49
0x00407a2f:	movl %esi, $0x1<UINT32>
0x00407a34:	testl %esi, %esi
0x00407a36:	je 37
0x00407a38:	movl %esi, %ecx
0x00407a3a:	cmpl %esi, $0xbff00000<UINT32>
0x00407a40:	jb 16
0x00407a42:	cmpl %esi, $0xbfff0000<UINT32>
0x00407a48:	ja 0x00407a52
0x00407a52:	movl %esi, 0x1(%esi)
0x00407a55:	andl %esi, $0xffff0000<UINT32>
0x00407a5b:	jmp 0x00407a60
0x00407a60:	xorl %eax, %eax
0x00407a62:	cmpl %eax, $0x32<UINT8>
0x00407a65:	je 0x00407a86
0x00407a67:	cmpw (%esi), $0x5a4d<UINT16>
0x00407a6c:	je 9
0x00407a6e:	subl %esi, $0x10000<UINT32>
0x00407a74:	incl %eax
0x00407a75:	jmp 0x00407a62
0x00407a86:	popl %fs:0
0x00407a8d:	addl %esp, $0x4<UINT8>
0x00407a90:	popa
0x00407a91:	ret

0x7559338a: Unknown Node	
