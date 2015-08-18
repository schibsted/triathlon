
package com.schibsted.triathlon.model.generated;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "container",
    "env",
    "id",
    "instances",
    "ports",
    "requirePorts",
    "cpus",
    "constraints",
    "mem"
})
@JsonFilter("filterConstraints")
public class Marathon {

    @JsonProperty("container")
    private Container container;
    @JsonProperty("env")
    private Env env;
    @JsonProperty("id")
    private String id;
    @JsonProperty("instances")
    private Integer instances;
    @JsonProperty("ports")
    private List<Integer> ports = new ArrayList<Integer>();
    @JsonProperty("requirePorts")
    private Boolean requirePorts;
    @JsonProperty("cpus")
    private Double cpus;
    @JsonProperty("constraints")
    private List<List<String>> constraints = new ArrayList<List<String>>();
    @JsonProperty("mem")
    private Integer mem;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The container
     */
    @JsonProperty("container")
    public Container getContainer() {
        return container;
    }

    /**
     * 
     * @param container
     *     The container
     */
    @JsonProperty("container")
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * 
     * @return
     *     The env
     */
    @JsonProperty("env")
    public Env getEnv() {
        return env;
    }

    /**
     * 
     * @param env
     *     The env
     */
    @JsonProperty("env")
    public void setEnv(Env env) {
        this.env = env;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The instances
     */
    @JsonProperty("instances")
    public Integer getInstances() {
        return instances;
    }

    /**
     * 
     * @param instances
     *     The instances
     */
    @JsonProperty("instances")
    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    /**
     * 
     * @return
     *     The ports
     */
    @JsonProperty("ports")
    public List<Integer> getPorts() {
        return ports;
    }

    /**
     * 
     * @param ports
     *     The ports
     */
    @JsonProperty("ports")
    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    /**
     * 
     * @return
     *     The requirePorts
     */
    @JsonProperty("requirePorts")
    public Boolean getRequirePorts() {
        return requirePorts;
    }

    /**
     * 
     * @param requirePorts
     *     The requirePorts
     */
    @JsonProperty("requirePorts")
    public void setRequirePorts(Boolean requirePorts) {
        this.requirePorts = requirePorts;
    }

    /**
     * 
     * @return
     *     The cpus
     */
    @JsonProperty("cpus")
    public Double getCpus() {
        return cpus;
    }

    /**
     * 
     * @param cpus
     *     The cpus
     */
    @JsonProperty("cpus")
    public void setCpus(Double cpus) {
        this.cpus = cpus;
    }

    /**
     * 
     * @return
     *     The constraints
     */
    @JsonProperty("constraints")
    public List<List<String>> getConstraints() {
        return constraints;
    }

    /**
     * 
     * @param constraints
     *     The constraints
     */
    @JsonProperty("constraints")
    public void setConstraints(List<List<String>> constraints) {
        this.constraints = constraints;
    }

    /**
     * 
     * @return
     *     The mem
     */
    @JsonProperty("mem")
    public Integer getMem() {
        return mem;
    }

    /**
     * 
     * @param mem
     *     The mem
     */
    @JsonProperty("mem")
    public void setMem(Integer mem) {
        this.mem = mem;
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
        return new HashCodeBuilder().append(container).append(env).append(id).append(instances).append(ports).append(requirePorts).append(cpus).append(constraints).append(mem).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Marathon) == false) {
            return false;
        }
        Marathon rhs = ((Marathon) other);
        return new EqualsBuilder().append(container, rhs.container).append(env, rhs.env).append(id, rhs.id).append(instances, rhs.instances).append(ports, rhs.ports).append(requirePorts, rhs.requirePorts).append(cpus, rhs.cpus).append(constraints, rhs.constraints).append(mem, rhs.mem).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
