/*
A Java-based HTTP stub server

Copyright (C) 2012 Alexander Zagniotov, Isa Goksu and Eric Mrak

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package by.stub.yaml.stubs;

import by.stub.utils.CollectionUtils;
import by.stub.utils.FileUtils;
import by.stub.utils.HandlerUtils;
import by.stub.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Alexander Zagniotov
 * @since 6/14/12, 1:09 AM
 */
public class StubRequest {

   private static final String REGEX_START = "^";
   public static final String AUTH_HEADER = "authorization";

   private String url;
   private ArrayList<String> method = new ArrayList<String>(1) {{
      add("GET");
   }};
   private String post;
   private byte[] file;
   private Map<String, String> headers = new HashMap<String, String>();
   private Map<String, String> query = new HashMap<String, String>();

   public StubRequest() {

   }

   public final ArrayList<String> getMethod() {
      final ArrayList<String> uppercase = new ArrayList<String>(method.size());

      for (final String string : method) uppercase.add(StringUtils.toUpper(string));

      return uppercase;
   }

   public void setMethod(final String newMethod) {
      this.method = new ArrayList<String>(1) {{
         add((StringUtils.isSet(newMethod) ? newMethod : "GET"));
      }};
   }

   public void setUrl(final String url) {
      this.url = url;
   }

   public final String getUrl() {
      if (query.isEmpty()) {
         return url;
      }

      final String queryString = CollectionUtils.constructQueryString(query);

      return String.format("%s?%s", url, queryString);
   }

   public String getPostBody() {
      if (file == null) {
         return FileUtils.enforceSystemLineSeparator(post);
      }
      final String utf8FileContent = new String(file, StringUtils.utf8Charset());
      return FileUtils.enforceSystemLineSeparator(utf8FileContent);
   }

   public void setPost(final String post) {
      this.post = post;
   }

   //Used by reflection when populating stubby admin page with stubbed information
   public String getPost() {
      return post;
   }

   public final Map<String, String> getHeaders() {
      return headers;
   }

   public void setHeaders(final Map<String, String> headers) {

      final Set<Map.Entry<String, String>> entrySet = headers.entrySet();
      for (final Map.Entry<String, String> entry : entrySet) {
         this.headers.put(StringUtils.toLower(entry.getKey()), entry.getValue());
      }
   }

   public void setQuery(final Map<String, String> query) {
      this.query = query;
   }

   public Map<String, String> getQuery() {
      return query;
   }


   public byte[] getFile() {
      return file;
   }

   public void setFile(final byte[] file) {
      this.file = file;
   }

   public static StubRequest createFromHttpServletRequest(final HttpServletRequest request) throws IOException {
      final StubRequest assertionRequest = new StubRequest();

      assertionRequest.setMethod(request.getMethod());
      assertionRequest.setUrl(request.getPathInfo());
      assertionRequest.setPost(HandlerUtils.extractPostRequestBody(request, "stubs"));

      final Enumeration<String> headerNamesEnumeration = request.getHeaderNames();
      final List<String> headerNames = headerNamesEnumeration == null ? new LinkedList<String>() : Collections.list(request.getHeaderNames());
      for (final String headerName : headerNames) {
         final String headerValue = request.getHeader(headerName);
         assertionRequest.getHeaders().put(StringUtils.toLower(headerName), headerValue);
      }

      assertionRequest.getQuery().putAll(CollectionUtils.constructParamMap(request.getQueryString()));

      return assertionRequest;
   }

   @Override
   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof StubRequest)) {
         return false;
      }

      final StubRequest dataStoreRequest = (StubRequest) o;

      if (!stringsMatch(dataStoreRequest.getPostBody(), this.getPostBody()))
         return false;

      if (!arraysMatch(dataStoreRequest.method, this.method)) {
         return false;
      }

      if (dataStoreRequest.url.startsWith(REGEX_START)) {
         if (!regexMatch(dataStoreRequest.url, this.url)) {
            return false;
         }
      } else if (!stringsMatch(dataStoreRequest.url, this.url)) {
         return false;
      }

      if (!dataStoreRequest.getHeaders().isEmpty()) {

         final Map<String, String> dataStoreHeadersCopy = new HashMap<String, String>(dataStoreRequest.getHeaders());
         dataStoreHeadersCopy.remove(StubRequest.AUTH_HEADER); //Auth header is dealt with in DataStore, after matching of assertion request
         if (dataStoreHeadersCopy.isEmpty()) {
            return true;
         } else {

            final Map<String, String> assertingHeaders = this.getHeaders();
            if (assertingHeaders.isEmpty() || !mapsMatch(dataStoreHeadersCopy, assertingHeaders)) {
               return false;
            }
         }
      }

      if (dataStoreRequest.getQuery().isEmpty()) {
         return true;
      }

      if (this.getQuery().isEmpty() || !mapsMatch(dataStoreRequest.getQuery(), this.getQuery())) {
         return false;
      }


      return true;
   }

   private boolean regexMatch(final String dataStoreRequestUrl, final String assertingUrl) {
      final Pattern pattern = Pattern.compile(dataStoreRequestUrl);

      return pattern.matcher(assertingUrl).matches();
   }


   private boolean stringsMatch(final String dataStoreValue, final String thisAssertingValue) {
      final boolean isAssertingValueSet = StringUtils.isSet(thisAssertingValue);
      final boolean isDatastoreValueSet = StringUtils.isSet(dataStoreValue);

      if (!isDatastoreValueSet) {
         return true;
      } else if (isDatastoreValueSet && !isAssertingValueSet) {
         return false;
      } else if (isDatastoreValueSet && isAssertingValueSet) {
         return dataStoreValue.equals(thisAssertingValue);
      }

      return true;
   }

   private boolean arraysMatch(final ArrayList<String> dataStoreArray, final ArrayList<String> thisAssertingArray) {
      final boolean isDatastoreArraySet = (dataStoreArray != null && dataStoreArray.size() != 0);
      final boolean isAssertingArraySet = (thisAssertingArray != null && thisAssertingArray.size() != 0);

      if (!isDatastoreArraySet) {
         return true;
      } else if (isDatastoreArraySet && !isAssertingArraySet) {
         return false;
      } else if (isDatastoreArraySet && isAssertingArraySet) {

         for (final String assertingArrayValue : thisAssertingArray) {
            if (dataStoreArray.contains(assertingArrayValue)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean mapsMatch(final Map<String, String> dataStoreMap, final Map<String, String> thisAssertingMap) {
      final Map<String, String> assertingMapCopy = new HashMap<String, String>(thisAssertingMap);
      final Map<String, String> dataStoreMapCopy = new HashMap<String, String>(dataStoreMap);
      dataStoreMapCopy.entrySet().removeAll(assertingMapCopy.entrySet());

      return dataStoreMapCopy.isEmpty();
   }

   @Override
   public final int hashCode() {
      int result = url.hashCode();
      result = 31 * result;
      result = 31 * result + getPostBody().hashCode();
      return result;
   }

   @Override
   public final String toString() {
      final StringBuffer sb = new StringBuffer();
      sb.append("StubRequest");
      sb.append("{url='").append(url).append('\'');
      sb.append(", method='").append(method).append('\'');
      sb.append(", post='").append(post).append('\'');
      sb.append(", file='").append(Arrays.toString(file)).append('\'');
      sb.append(", headers=").append(headers);
      sb.append(", query=").append(query);
      sb.append('}');
      return sb.toString();
   }

}