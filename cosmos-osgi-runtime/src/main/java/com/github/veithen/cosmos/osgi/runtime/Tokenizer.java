/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2022 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.cosmos.osgi.runtime;

final class Tokenizer {
    private static final String whitespace = " \t\n\r";

    private final String s;
    private int index;

    public Tokenizer(String s) {
        this.s = s;
    }

    private void skipWhiteSpace() {
        for (int len = s.length();
                index < len && whitespace.indexOf(s.charAt(index)) != -1;
                index++) {
            // Just loop
        }
    }

    String getToken(String terminals) {
        skipWhiteSpace();
        int begin = index;
        for (int len = s.length();
                index < len && terminals.indexOf(s.charAt(index)) == -1;
                index++) {
            // Just loop
        }
        int end = index;
        skipWhiteSpace();
        for (; end > begin && whitespace.indexOf(s.charAt(end - 1)) != -1; end--) {
            // Just loop
        }
        return s.substring(begin, end);
    }

    String getString(String terminals) throws ParseException {
        skipWhiteSpace();
        int len = s.length();
        if (index < len) {
            if (s.charAt(index) == '\"') {
                StringBuffer sb = new StringBuffer();
                index++;
                for (; index < len; index++) {
                    char c = s.charAt(index);
                    if (c == '\\') {
                        index++;
                        if (index == len) {
                            throw new ParseException("Expected more input after escape character");
                        }
                        sb.append(s.charAt(index));
                    } else if (c == '\"') {
                        break;
                    } else {
                        sb.append(c);
                    }
                }
                if (index == len) {
                    throw new ParseException("Unclosed quoted string");
                }
                // If we get here, then the current character is a quote; skip it
                index++;
                skipWhiteSpace();
                return sb.toString();
            } else {
                return getToken(terminals);
            }
        }
        return null;
    }

    int getChar() {
        return index == s.length() ? -1 : s.charAt(index++);
    }

    boolean skipIf(char c) {
        if (index == s.length()) {
            return false;
        } else if (s.charAt(index) == c) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    boolean hasMoreTokens() {
        return index < s.length();
    }
}
