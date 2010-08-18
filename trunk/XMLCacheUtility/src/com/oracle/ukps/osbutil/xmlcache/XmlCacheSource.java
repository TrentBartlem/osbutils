/*
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.ukps.osbutil.xmlcache;

import java.util.Properties;

import org.apache.xmlbeans.XmlObject;

/**
 * Interface to a XML source to be used by the cache utility.
 * 
 * <p>This interface must be implemented by any custom XML source that is
 * used by the XML cache utility.</p>
 * 
 * <p>The cache utility is responsible for creating an instance of the custom
 * source and does so via reflection to find a no-argument constructor.
 * Therefore a custom XML source <b>MUST implement a no-argument constructor.
 * </b></p>
 * 
 * 
 * @see XmlCacheFileSource
 * 
 */

public interface XmlCacheSource {

	/**
	 * Search the XML source for the XML identified by key
	 * 
	 * @param key	The key to use for locating the XML
	 * @return		The XML associated for the key or null if not found
	 */
	
	public XmlObject readSource(String key);
	
	/**
	 * Configure the source
	 * 
	 * <p>This method is invoked by the XmlCacheUtility when it is creating the
	 * XmlCacheSource.</p>
	 * 
	 * @param configuration	The configuration properties already loaded by the
	 * XmlCacheUtility
	 * @param base The configuration base key that should be considered the
	 * basis for keys associated with this source
	 * @throws An exception if the configuration will result in a source that
	 * is not usable
	 */
	
	public void configure(Properties configuration, String base) throws Exception ;
}
