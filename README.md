# pokitdok-clojure

A small PokitDok API client

## Usage

```clj

(require '[com.pokitdok.client :as pokitdok])

;; Make a client
(def c (pokitdok/create-client CLIENT-ID CLIENT-SECRET))

;; Grab an oauth token
(pokitdok/connect! c)

;; Make a specific request
(pokitdok/get-activity c "42")

;; Or make a custom request using clj-http param maps
(pokitdok/request c {:method :get
                     :url "/activities 42"})
```

`com.pokitdok.client` contains functions that implement the v4 PokitDok API.

## Extension points

com.pokitdok.impl/HTTPClient is available to piggyback your own HTTP library.
By default, clj-http is used.

## Contributions

Pull requests and issues are gladly accepted.

## License

Copyright © 2015 Pokitdok, Inc.

Distributed under the MIT Licence

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

