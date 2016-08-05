package net.sizovs.capital

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version

class EmailTemplate {

    Template template

    EmailTemplate(String name) {
        def cfg = new Configuration()
        cfg.setClassForTemplateLoading(Class, "/freemarker/")
        cfg.incompatibleImprovements = new Version(2, 3, 20)
        cfg.defaultEncoding = "UTF-8"
        cfg.outputEncoding = "UTF-8"
        cfg.locale = Locale.US
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        this.template = cfg.getTemplate("${name}.ftl")
    }

    String render(Map inputs) {
        def writer = new StringWriter()
        template.process(inputs, writer)
        writer.toString()
    }


}
