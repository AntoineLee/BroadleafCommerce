package org.broadleafcommerce.common.web.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.common.web.processor.attributes.ResourceTagAttributes;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.springframework.stereotype.Component;

/**
 * Adds &lt;link&gt; tags to the model that preload resources.
 * <p>
 * This is useful in combination with bundling where one bundle might depend on another and must wait for the other to
 * finish before it can be added to the DOM. Since the script isn't immediately in the DOM, the browser doesn't download
 * the resource until it's added to the DOM, increasing the time before the bundle can be used.
 * <p>
 * This processor adds preload link tags which tell the browser to preload (download) a resource even though it isn't
 * yet in the DOM. Doing so decreases the latency when the script is ready to execute.
 *
 * @author Jacob Mitash
 */
@Component("blResourcePreloadProcessor")
@ConditionalOnTemplating
public class ResourcePreloadProcessor extends AbstractResourceProcessor {

    @Override
    public String getName() {
        return "bundlepreload";
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected BroadleafTemplateModel buildModelBundled(List<String> files, ResourceTagAttributes resourceTagAttributes, BroadleafTemplateContext context) {
        BroadleafTemplateModel model = context.createModel();

        final String bundleResourceName = bundlingService.resolveBundleResourceName(resourceTagAttributes.name(),
                resourceTagAttributes.mappingPrefix(),
                files);

        final String bundleUrl = getBundleUrl(bundleResourceName, context);

        BroadleafTemplateElement bundlePreload = buildPreloadElement(bundleUrl, context);
        model.addElement(bundlePreload);

        return model;
    }

    @Override
    protected BroadleafTemplateModel buildModelUnbundled(List<String> files, ResourceTagAttributes resourceTagAttributes, BroadleafTemplateContext context) {
        BroadleafTemplateModel model = context.createModel();

        for (final String file : files) {
            final String fullFileName = getFullUnbundledFileName(file, resourceTagAttributes, context);
            BroadleafTemplateElement element = buildPreloadElement(fullFileName, context);
            model.addElement(element);
        }

        return model;
    }

    /**
     * Builds a preload link for the given path
     * @param href the path of the file to create the link with
     * @param context the context of the bundlepreload tag
     * @return a link element linking to the given resource
     */
    protected BroadleafTemplateElement buildPreloadElement(String href, BroadleafTemplateContext context) {
        final String as = getAs(href);
        Map<String, String> attributes = getPreloadAttributes(href, as);

        return context.createStandaloneElement("link", attributes, true);
    }

    /**
     * Builds a map of the attributes that should be put on the &lt;link&gt; tag.
     * @param href the href of the resource to preload
     * @param as the value the "as" attribute should have or null if it shouldn't be included
     * @return a map of attributes to place on the link tag
     */
    protected Map<String, String> getPreloadAttributes(String href, String as) {
        Map<String, String> attributes = new HashMap<>();

        attributes.put("href", href);
        attributes.put("rel", "preload");
        if (as != null) {
            attributes.put("as", as);
        }

        return attributes;
    }

    /**
     * Gets the "as" attribute for the link based off of the file name
     * @param file the name of the file
     * @return an appropriate "as" value or null if none was found
     */
    protected String getAs(String file) {
        if (file.endsWith(".js")) {
            return "script";
        } else if (file.endsWith(".css")) {
            return "style";
        } else {
            return null;
        }
    }

}
