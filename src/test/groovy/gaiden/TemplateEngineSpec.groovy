/*
 * Copyright 2013 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaiden

import spock.lang.Specification

class TemplateEngineSpec extends Specification {

    def "'make' should make a page with a template"() {
        setup:
        def templateText = new File("src/test/resources/templates/simple-template.html").text
        def templateEngine = new TemplateEngine(new File("/output"), templateText, [title: "Gaiden", tocPath: "toc.html"])
        def binding = [
            content: "<h1>Hello</h1>",
            outputPath: "aaa.html"
        ]

        expect:
        templateEngine.make(binding) == '''<html>
                     |<head>
                     |    <title>Gaiden</title>
                     |</head>
                     |<body>
                     |<h1>Hello</h1>
                     |</body>
                     |</html>
                     |'''.stripMargin()
    }

    def "'make' should evaluate the 'resource'"() {
        setup:
        def templateText = '${resource("' + resourcePath + '")}'
        def templateEngine = new TemplateEngine(new File("/output"), templateText, [title: "Gaiden", tocPath: "toc.html"])
        def binding = [outputPath: outputPath]

        expect:
        templateEngine.make(binding) == expected

        where:
        resourcePath   | outputPath     | expected
        "/aaa/bbb.txt" | "ccc/ddd.html" | "../aaa/bbb.txt"
        "aaa/bbb.txt"  | "ccc/ddd.html" | "aaa/bbb.txt"
        ""             | "ccc/ddd.html" | ""
    }

    def "'make' should evaluate the 'tocPath'"() {
        setup:
        def templateText = '${tocPath}'
        def templateEngine = new TemplateEngine(new File("/output"), templateText, [title: "Gaiden", tocPath: "toc.html"])
        def binding = [outputPath: outputPath]

        expect:
        templateEngine.make(binding) == expected

        where:
        outputPath     | expected
        "ddd.html"     | "toc.html"
        "ccc/ddd.html" | "../toc.html"
    }

}
