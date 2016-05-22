/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2.org.analysis.environment;

import v2.org.analysis.value.LongValue;

/**
 *
 * @author zunc
 */
public class MemoryWriteStream {

	private final Memory _mem;
	private long _offset;

	public MemoryWriteStream(Memory mem, long startOffset) {
		_mem = mem;
		_offset = startOffset;
	}

	public void writeByte(byte val) {
		_mem.setByteMemoryValue(_offset, new LongValue(val));
		_offset++;
	}

	public void writeWord(int val) {
		_mem.setWordMemoryValue(_offset, new LongValue(val));
		_offset += 2;
	}

	public void writeDoubleWord(long val) {
		_mem.setWordMemoryValue(_offset, new LongValue(val));
		_offset += 4;
	}

}
