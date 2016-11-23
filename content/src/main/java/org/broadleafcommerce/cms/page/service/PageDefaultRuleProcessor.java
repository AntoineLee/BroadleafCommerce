/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.page.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * By default, this rule processor combines all of the rules from
 * {@link org.broadleafcommerce.cms.page.domain.Page#getPageMatchRules()}
 * into a single MVEL expression.
 *
 * @author bpolster.
 *
 */
@Service("blPageDefaultRuleProcessor")
public class PageDefaultRuleProcessor { //TODO microservices deal with rule processors  extends AbstractRuleProcessor<PageDTO> {
    private static final Log LOG = LogFactory.getLog(PageDefaultRuleProcessor.class);

    /**
     * Returns true if all of the rules associated with the passed in <code>Page</code>
     * item match based on the passed in vars.
     *
     * Also returns true if no rules are present for the passed in item.
     *
     * @param sc - a page item to test
     * @param vars - a map of objects used by the rule MVEL expressions
     * @return the result of the rule checks
     */
    public boolean checkForMatch(PageDTO page, Map<String, Object> vars) {
        String ruleExpression = page.getRuleExpression();

        if (ruleExpression != null) {
            if (LOG.isTraceEnabled())  {
                LOG.trace("Processing content rule for page with id " + page.getId() +".   Value = " + StringUtil.sanitize(ruleExpression));
            }
            //TODO microservices - deal with rule processors
            //boolean result = executeExpression(ruleExpression, vars);
            boolean result = false;
            if (! result) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Page failed to pass rule and will not be included for Page with id " + page.getId() +".   Value = " + StringUtil.sanitize(ruleExpression));
                }
            }

            return result;
        } else {
            // If no rule found, then consider this a match.
            return true;
        }
    }
}