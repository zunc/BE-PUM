0x00401000:	movl %ebx, $0x0<UINT32>
0x00401005:	movl %edx, $0x401035<UINT32>
0x0040100a:	cmpl %eax, $0x0<UINT8>
0x0040100d:	jl 0x00401035
0x0040100f:	cmpl %eax, $0x2<UINT8>
0x00401035:	call 0x00401044
0x00401044:	jmp ExitProcess@kernel32.dll
ExitProcess@kernel32.dll: Exit Node	
0x00401012:	jg 0x00401035
0x00401014:	movl %ebx, %eax
0x00401016:	imull %ebx, %ebx, $0x8<UINT8>
0x00401019:	addl %ebx, $0x401021<UINT32>
0x0040101f:	jmp 0x00401021
0x00401021:	addl %eax, $0x1<UINT8>
