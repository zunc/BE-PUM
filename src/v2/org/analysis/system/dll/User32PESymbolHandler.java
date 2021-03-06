package v2.org.analysis.system.dll;

import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.SymbolFinder;
import org.jakstab.loader.ExportedSymbol;
import org.jakstab.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class User32PESymbolHandler implements SymbolFinder {
	private User32PEHandler module;
	private Map<AbsoluteAddress, String> symbols;

	/**
	 * @param user32peHandler
	 */
	public User32PESymbolHandler(User32PEHandler user32peHandler) {
		super();
		this.module = user32peHandler;
		symbols = new HashMap<AbsoluteAddress, String>();
		// System.out.println("Import Symbols");
		for (Map.Entry<AbsoluteAddress, Pair<String, String>> e : this.module.getImportTable().entrySet()) {
			symbols.put(e.getKey(), e.getValue().getRight() + "@" + e.getValue().getLeft());
			// System.out.println("Addr: 0x" +
			// Long.toHexString(e.getKey().getValueOperand()) +
			// " - " + e.getValueOperand().getRight() + "@" +
			// e.getValueOperand().getLeft());
		}

		// System.out.println("Export Symbols");
		for (ExportedSymbol exSym : user32peHandler.getExportedSymbols()) {
			symbols.put(exSym.getAddress(), exSym.getName());
			// System.out.println(exSym.toString());
		}
	}

	@Override
	public String getSymbolFor(long address) {
		return getSymbolFor(new AbsoluteAddress(address));
	}

	@Override
	public String getSymbolFor(AbsoluteAddress va) {
		String symbol = symbols.get(va);
		if (symbol != null)
			return symbol;
		else
			return va.toString();
	}

	@Override
	public boolean hasSymbolFor(AbsoluteAddress va) {
		return symbols.containsKey(va);
	}
}
