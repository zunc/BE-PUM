/**
 * Project: BE-PUMv2
 * Package name: v2.org.analysis.apihandle.winapi.gdi32
 * File name: Gdi32DLL.java
 * Created date: Aug 7, 2015
 * Description:
 */
package v2.org.analysis.apihandle.winapi.gdi32;

import v2.org.analysis.apihandle.winapi.structures.Wingdi.LOGPALETTE;
import v2.org.analysis.apihandle.winapi.structures.Wingdi.PALETTEENTRY;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * @author Yen Nguyen
 *
 */
public interface Gdi32DLL extends StdCallLibrary {

	Gdi32DLL INSTANCE = (Gdi32DLL) Native.loadLibrary("gdi32", Gdi32DLL.class, W32APIOptions.DEFAULT_OPTIONS);
	Gdi32DLL SYNC_INSTANCE = (Gdi32DLL) Native.synchronizedLibrary(INSTANCE);

	/**
	 * The AnimatePalette function replaces entries in the specified logical
	 * palette.
	 * 
	 * @param hpal
	 *            A handle to the logical palette.
	 * 
	 * @param iStartIndex
	 *            The first logical palette entry to be replaced.
	 * 
	 * @param cEntries
	 *            The number of entries to be replaced.
	 * 
	 * @param pe
	 *            A pointer to the first member in an array of PALETTEENTRY
	 *            structures used to replace the current entries.
	 * 
	 * @return If the function succeeds, the return value is nonzero. If the
	 *         function fails, the return value is zero.
	 */
	int AnimatePalette(
	/* _In_ */HANDLE hpal,
	/* _In_ */UINT iStartIndex,
	/* _In_ */UINT cEntries,
	/* _In_ */PALETTEENTRY pe);

	/**
	 * The AddFontResource function adds the font resource from the specified
	 * file to the system font table. The font can subsequently be used for text
	 * output by any application.
	 * 
	 * @param lpszFilename
	 *            A pointer to a null-terminated character string that contains
	 *            a valid font file name. This parameter can specify any of the
	 *            following files.
	 * 
	 * @return If the function succeeds, the return value specifies the number
	 *         of fonts added. If the function fails, the return value is zero.
	 *         No extended error information is available.
	 */
	int AddFontResource(/* _In_ */WString lpszFilename);

	/**
	 * The GetStockObject function retrieves a handle to one of the stock pens,
	 * brushes, fonts, or palettes.
	 * 
	 * @param fnObject
	 *            The type of stock object. This parameter can be one of the
	 *            following values.
	 * 
	 * @return If the function succeeds, the return value is a handle to the
	 *         requested logical object. If the function fails, the return value
	 *         is NULL.
	 */
	HANDLE GetStockObject(/* _In_ */int fnObject);

	/**
	 * The GdiGetBatchLimit function returns the maximum number of function
	 * calls that can be accumulated in the calling thread's current batch. The
	 * system flushes the current batch whenever this limit is exceeded.
	 * 
	 * @return If the function succeeds, the return value is the batch limit. If
	 *         the function fails, the return value is zero.
	 */
	DWORD GdiGetBatchLimit();

	/**
	 * The GetBkColor function returns the current background color for the
	 * specified device context.
	 * 
	 * @param hdc
	 *            Handle to the device context whose background color is to be
	 *            returned.
	 * 
	 * @return If the function succeeds, the return value is a COLORREF value
	 *         for the current background color. If the function fails, the
	 *         return value is CLR_INVALID.
	 */
	DWORD GetBkColor(HDC hdc);

	/**
	 * The SetBkColor function sets the current background color to the
	 * specified color value, or to the nearest physical color if the device
	 * cannot represent the specified color value.
	 * 
	 * @param hdc
	 *            A handle to the device context.
	 * 
	 * @param crColor
	 *            The new background color. To make a COLORREF value, use the
	 *            RGB macro.
	 * 
	 * @return If the function succeeds, the return value specifies the previous
	 *         background color as a COLORREF value. If the function fails, the
	 *         return value is CLR_INVALID.
	 */
	DWORD SetBkColor(
	/* _In_ */HDC hdc,
	/* _In_ */DWORD crColor);

	/**
	 * The CreateCompatibleDC function creates a memory device context (DC)
	 * compatible with the specified device.
	 * 
	 * @param hdc
	 *            A handle to an existing DC. If this handle is NULL, the
	 *            function creates a memory DC compatible with the application's
	 *            current screen.
	 * 
	 * @return If the function succeeds, the return value is the handle to a
	 *         memory DC. If the function fails, the return value is NULL.
	 */
	HDC CreateCompatibleDC(/* _In_ */HDC hdc);

	/**
	 * The CreatePalette function creates a logical palette.
	 * 
	 * @param lplgpl
	 *            A pointer to a LOGPALETTE structure that contains information
	 *            about the colors in the logical palette.
	 * 
	 * @return If the function succeeds, the return value is a handle to a
	 *         logical palette. If the function fails, the return value is NULL.
	 */
	HANDLE CreatePalette(/* _In_ *//* const */LOGPALETTE lplgpl);

	/**
	 * The DeleteObject function deletes a logical pen, brush, font, bitmap,
	 * region, or palette, freeing all system resources associated with the
	 * object. After the object is deleted, the specified handle is no longer
	 * valid.
	 * 
	 * @param hObject
	 *            A handle to a logical pen, brush, font, bitmap, region, or
	 *            palette.
	 * 
	 * @return If the function succeeds, the return value is nonzero. If the
	 *         specified handle is not valid or is currently selected into a DC,
	 *         the return value is zero.
	 */
	BOOL DeleteObject(HANDLE hObject);

	/**
	 * The SetTextAlign function sets the text-alignment flags for the specified
	 * device context.
	 * 
	 * @param hdc
	 *            A handle to the device context.
	 * 
	 * @param fMode
	 *            The text alignment by using a mask of the values in the
	 *            following list. Only one flag can be chosen from those that
	 *            affect horizontal and vertical alignment. In addition, only
	 *            one of the two flags that alter the current position can be
	 *            chosen.
	 * 
	 * @return If the function succeeds, the return value is the previous
	 *         text-alignment setting. If the function fails, the return value
	 *         is GDI_ERROR.
	 */
	UINT SetTextAlign(/* _In_ */HDC hdc,/* _In_ */UINT fMode);

	/**
	 * The StrokePath function renders the specified path by using the current
	 * pen.
	 * 
	 * @param hdc
	 *            Handle to a device context that contains the completed path.
	 * 
	 * @return If the function succeeds, the return value is nonzero. If the
	 *         function fails, the return value is zero.
	 */
	BOOL StrokePath( /* _In_ */HDC hdc);

	/**
	 * The StrokeAndFillPath function closes any open figures in a path, strokes
	 * the outline of the path by using the current pen, and fills its interior
	 * by using the current brush.
	 * 
	 * @param hdc
	 *            A handle to the device context.
	 * 
	 * @return If the function succeeds, the return value is nonzero. If the
	 *         function fails, the return value is zero.
	 */
	BOOL StrokeAndFillPath(/* _In_ */HDC hdc);

	/**
	 * The DPtoLP function converts device coordinates into logical coordinates.
	 * The conversion depends on the mapping mode of the device context, the
	 * settings of the origins and extents for the window and viewport, and the
	 * world transformation.
	 * 
	 * @param hdc
	 *            A handle to the device context.
	 * 
	 * @param lpPoints
	 *            A pointer to an array of POINT structures. The x- and
	 *            y-coordinates contained in each POINT structure will be
	 *            transformed.
	 * 
	 * @param nCount
	 *            The number of points in the array.
	 * 
	 * @return If the function succeeds, the return value is nonzero. If the
	 *         function fails, the return value is zero.
	 */
	BOOL DPtoLP(
	/* _In_ */HDC hdc,
	/* _Inout_ */POINT lpPoints,
	/* _In_ */int nCount);

	// int EnumFontFamiliesEx(
	// /*_In_*/ HDC hdc,
	// /*_In_*/ LOGFONT lpLogfont,
	// /*_In_*/ FONTENUMPROC lpEnumFontFamExProc,
	// /*_In_*/ LPARAM lParam,
	// DWORD dwFlags
	// );
}
