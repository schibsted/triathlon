
package com.schibsted.triathlon.model.generated;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "archaius.deployment.environment"
})
public class Env {

    @JsonProperty("archaius.deployment.environment")
    private String archaius_deployment_environment;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The archaius_deployment_environment
     */
    @JsonProperty("archaius.deployment.environment")
    public String getArchaius_deployment_environment() {
        return archaius_deployment_environment;
    }

    /**
     * 
     * @param archaius_deployment_environment
     *     The archaius.deployment.environment
     */
    @JsonProperty("archaius.deployment.environment")
    public void setArchaius_deployment_environment(String archaius_deployment_environment) {
        this.archaius_deployment_environment = archaius_deployment_environment;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(archaius_deployment_environment).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Env) == false) {
            return false;
        }
        Env rhs = ((Env) other);
        return new EqualsBuilder().append(archaius_deployment_environment, rhs.archaius_deployment_environment).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
