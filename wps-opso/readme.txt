Ontology for processing service orchestration (OPSO) is developed to describe workflows
in a way that makes them more adaptable, sharable, reusable and traceable. OPSO represents 
three aspects of workflows: template, instance and provenance. Workflow template allows 
users to describe what kind of data and processing are needed in a workflow in an abstract
way. Workflow instances are built when workflow templates are matched to distributed 
services, which are executable. Workflow provenance is generated during the workflow 
execution. It records data used, process steps and data dependencies between processes, 
which could not only document how the results come but also help refine or modify the 
workflow template and instance. 

OPSO is supported by GeoJModelBuilder, which is available on 
https://github.com/geoprocessing/GeoJModelBuilder.

This module is used to publish workflow instance as a WPS process.