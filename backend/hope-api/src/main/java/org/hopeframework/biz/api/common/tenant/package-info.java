/**
 * Business data scope. The open-source edition uses one fixed community scope.
 * A commercial edition may provide another resolver without changing business services.
 *
 * <p>Code in this package must never trust a tenant ID supplied as a normal
 * request parameter. Tenant identity is resolved from trusted request context
 * and verified against the authenticated user's membership.</p>
 */
package org.hopeframework.biz.api.common.tenant;
