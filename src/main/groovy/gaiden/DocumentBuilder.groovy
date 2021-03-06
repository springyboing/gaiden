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

/**
 * A document builder builds from a document source to a document.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class DocumentBuilder {

    private File templateFile
    private Map baseBinding

    private PageBuilder pageBuilder
    private TocBuilder tocBuilder

    private File outputDirectoryFile

    DocumentBuilder() {
        this.templateFile = Holders.config.templatePathFile
        this.baseBinding = [
            title: Holders.config.title,
            tocPath: Holders.config.tocOutputPath,
        ]
        this.outputDirectoryFile = Holders.config.outputDirectoryFile

        def templateEngine = createTemplateEngine()
        this.pageBuilder = new PageBuilder(templateEngine)
        this.tocBuilder = new TocBuilder(templateEngine)
    }

    DocumentBuilder(File templateFile, PageBuilder pageBuilder, TocBuilder tocBuilder, File outputDirectoryFile, Map baseBinding) {
        this.templateFile = templateFile
        this.pageBuilder = pageBuilder
        this.tocBuilder = tocBuilder
        this.outputDirectoryFile = outputDirectoryFile
        this.baseBinding = baseBinding
    }

    /**
     * Builds a document from a document source.
     *
     * @param documentSource the document source to be built
     * @return {@link Document}'s instance
     */
    Document build(DocumentSource documentSource) {
        def pages = buildPages(documentSource)
        def toc = buildToc(pages)
        new Document(toc: toc, pages: pages)
    }

    private Toc buildToc(List<Page> pages) {
        tocBuilder.build(pages)
    }

    private List<Page> buildPages(DocumentSource documentSource) {
        documentSource.pageSources.collect { pageSource ->
            pageBuilder.build(pageSource)
        }
    }

    private TemplateEngine createTemplateEngine() {
        new TemplateEngine(outputDirectoryFile, templateFile.text, baseBinding)
    }

}
