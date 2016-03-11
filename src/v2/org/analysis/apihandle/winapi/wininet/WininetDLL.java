/**
 * Project: BE-PUMv2
 * Package name: v2.org.analysis.apihandle.winapi.wininet
 * File name: WininetDLL.java
 * Created date: Aug 28, 2015
 * Description:
 */
package v2.org.analysis.apihandle.winapi.wininet;

import java.nio.ByteBuffer;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * @author Yen Nguyen
 *
 */
public interface WininetDLL extends StdCallLibrary {
	WininetDLL INSTANCE = (WininetDLL) Native.loadLibrary("wininet", WininetDLL.class, W32APIOptions.DEFAULT_OPTIONS);
	WininetDLL SYNC_INSTANCE = (WininetDLL) Native.synchronizedLibrary(INSTANCE);

	/**
	 * Sets an Internet option.
	 * 
	 * @param hInternet
	 *            Handle on which to set information.
	 * 
	 * @param dwOption
	 *            Internet option to be set. This can be one of the Option Flags
	 *            values.
	 * 
	 * @param lpBuffer
	 *            Pointer to a buffer that contains the option setting.
	 * 
	 * @param dwBufferLength
	 *            Size of the lpBuffer buffer. If lpBuffer contains a string,
	 *            the size is in TCHARs. If lpBuffer contains anything other
	 *            than a string, the size is in bytes.
	 * 
	 * @return Returns TRUE if successful, or FALSE otherwise. To get a specific
	 *         error message, call GetLastError.
	 */
	BOOL InternetSetOption(
	/* _In_ *//* HINTERNET */HANDLE hInternet,
	/* _In_ */DWORD dwOption,
	/* _In_ */LPVOID lpBuffer,
	/* _In_ */DWORD dwBufferLength);

	/**
	 * Initializes an application's use of the WinINet functions.
	 * 
	 * @param lpszAgent
	 *            Pointer to a null-terminated string that specifies the name of
	 *            the application or entity calling the WinINet functions. This
	 *            name is used as the user agent in the HTTP protocol.
	 * 
	 * @param dwAccessType
	 *            Type of access required. This parameter can be one of the
	 *            following values.
	 * 
	 * @param lpszProxyName
	 *            Pointer to a null-terminated string that specifies the name of
	 *            the proxy server(s) to use when proxy access is specified by
	 *            setting dwAccessType to INTERNET_OPEN_TYPE_PROXY. Do not use
	 *            an empty string, because InternetOpen will use it as the proxy
	 *            name. The WinINet functions recognize only CERN type proxies
	 *            (HTTP only) and the TIS FTP gateway (FTP only). If Microsoft
	 *            Internet Explorer is installed, these functions also support
	 *            SOCKS proxies. FTP requests can be made through a CERN type
	 *            proxy either by changing them to an HTTP request or by using
	 *            InternetOpenUrl. If dwAccessType is not set to
	 *            INTERNET_OPEN_TYPE_PROXY, this parameter is ignored and should
	 *            be NULL. For more information about listing proxy servers, see
	 *            the Listing Proxy Servers section of Enabling Internet
	 *            Functionality.
	 * 
	 * @param lpszProxyBypass
	 *            Pointer to a null-terminated string that specifies an optional
	 *            list of host names or IP addresses, or both, that should not
	 *            be routed through the proxy when dwAccessType is set to
	 *            INTERNET_OPEN_TYPE_PROXY. The list can contain wildcards. Do
	 *            not use an empty string, because InternetOpen will use it as
	 *            the proxy bypass list. If this parameter specifies the
	 *            "<local>" macro, the function bypasses the proxy for any host
	 *            name that does not contain a period. By default, WinINet will
	 *            bypass the proxy for requests that use the host names
	 *            "localhost", "loopback", "127.0.0.1", or "[::1]". This
	 *            behavior exists because a remote proxy server typically will
	 *            not resolve these addresses properly.
	 * 
	 * @param dwFlags
	 *            Options. This parameter can be a combination of the following
	 *            values.
	 * 
	 * @return Returns a valid handle that the application passes to subsequent
	 *         WinINet functions. If InternetOpen fails, it returns NULL. To
	 *         retrieve a specific error message, call GetLastError.
	 */
	HANDLE InternetOpen(
	/* _In_ */String lpszAgent,
	/* _In_ */DWORD dwAccessType,
	/* _In_ */String lpszProxyName,
	/* _In_ */String lpszProxyBypass,
	/* _In_ */DWORD dwFlags);

	/**
	 * Opens a resource specified by a complete FTP or HTTP URL.
	 * 
	 * @param hInternet
	 *            The handle to the current Internet session. The handle must
	 *            have been returned by a previous call to InternetOpen.
	 * 
	 * @param lpszUrl
	 *            A pointer to a null-terminated string variable that specifies
	 *            the URL to begin reading. Only URLs beginning with ftp:,
	 *            http:, or https: are supported.
	 * 
	 * @param lpszHeaders
	 *            A pointer to a null-terminated string that specifies the
	 *            headers to be sent to the HTTP server. For more information,
	 *            see the description of the lpszHeaders parameter in the
	 *            HttpSendRequest function.
	 * 
	 * @param dwHeadersLength
	 *            The size of the additional headers, in TCHARs. If this
	 *            parameter is -1L and lpszHeaders is not NULL, lpszHeaders is
	 *            assumed to be zero-terminated (ASCIIZ) and the length is
	 *            calculated.
	 * 
	 * @param dwFlags
	 *            This parameter can be one of the following values.
	 * 
	 * @param dwContext
	 *            A pointer to a variable that specifies the application-defined
	 *            value that is passed, along with the returned handle, to any
	 *            callback functions.
	 * 
	 * @return Returns a valid handle to the URL if the connection is
	 *         successfully established, or NULL if the connection fails. To
	 *         retrieve a specific error message, call GetLastError. To
	 *         determine why access to the service was denied, call
	 *         InternetGetLastResponseInfo.
	 */
	HANDLE InternetOpenUrl(
	/* _In_ */HANDLE hInternet,
	/* _In_ */String lpszUrl,
	/* _In_ */String lpszHeaders,
	/* _In_ */DWORD dwHeadersLength,
	/* _In_ */DWORD dwFlags,
	/* _In_ */DWORD_PTR dwContext);

	/**
	 * Reads data from a handle opened by the InternetOpenUrl, FtpOpenFile, or
	 * HttpOpenRequest function.
	 * 
	 * @param hFile
	 *            Handle returned from a previous call to InternetOpenUrl,
	 *            FtpOpenFile, or HttpOpenRequest.
	 * 
	 * @param lpBuffer
	 *            Pointer to a buffer that receives the data.
	 * 
	 * @param dwNumberOfBytesToRead
	 *            Number of bytes to be read.
	 * 
	 * @param lpdwNumberOfBytesRead
	 *            Pointer to a variable that receives the number of bytes read.
	 *            InternetReadFile sets this value to zero before doing any work
	 *            or error checking.
	 * 
	 * @return Returns TRUE if successful, or FALSE otherwise. To get extended
	 *         error information, call GetLastError. An application can also use
	 *         InternetGetLastResponseInfo when necessary.
	 */
	BOOL InternetReadFile(
	/* _In_ */HANDLE hFile,
	/* _Out_ */ByteBuffer lpBuffer,
	/* _In_ */int dwNumberOfBytesToRead,
	/* _Out_ */DWORDByReference lpdwNumberOfBytesRead);

	/**
	 * Closes a single Internet handle.
	 * 
	 * @param hInternet
	 *            Handle to be closed.
	 * 
	 * @return Returns TRUE if the handle is successfully closed, or FALSE
	 *         otherwise. To get extended error information, call GetLastError.
	 */
	BOOL InternetCloseHandle(
	/* _In_ */HANDLE hInternet);
}
