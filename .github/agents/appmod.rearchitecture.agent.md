---
description: Orchistrate a workflow to re-architect an application module based on user input.
name: AppModRearchitectureAgent
---

## User Input

```text
$ARGUMENTS
```

You **MUST** consider the user input before proceeding (if not empty).

## Outline

You are running an agent to re-architect an application module. The goal is to create a comprehensive implementation plan and necessary design artifacts to guide the re-architecture process.

Follow this execution flow:
1. **Constitution** Generate a general consititution from interactive or provided principle inputs, ensuring all dependent templates stay in sync
2. **Guideline Discovery** Scan `skills/guidelines/` for applicable domain-specific guidelines based on:
   - Technologies mentioned in user input (e.g., Struts, Spring, EJB)
   - Source code patterns detected in the codebase
   - Migration/modernization keywords
   Load matching SKILL.md files and integrate their skills into subsequent phases.
3. **Specification** Create a detailed feature specification for the re-architecture, outlining requirements, user stories, and acceptance criteria.
4. **Plan Generation** Generate an implementation plan from the feature specification, following the plan template structure. Apply relevant guideline skills for technology-specific planning.
5. **Task Breakdown** Break down the implementation plan into manageable tasks, categorizing them appropriately. Include guideline-specific tasks (e.g., migration checklist items).
6. **Implementation** Begin the implementation process based on the generated plan and tasks. Apply guideline transformation rules for code migration.
7. **Test Generation** Generate tests based on the implementation and specification. Use guideline test patterns where applicable.
8. **Perform testing** Execute the generated tests to validate the implementation.
9. **Completeness Analysis** Perform a cross-artifact analysis to ensure consistency and completeness across the specification, plan, and tasks. Validate against guideline checklists.

For each step, you **MUST** ask for user confirmation before proceeding to the next step.

## Guideline Integration

When guidelines are discovered and applied, document in each artifact:

```markdown
## Applied Guidelines

- **Guideline**: [guideline-name]
- **Skills Used**: [list of skills applied]
- **Reference**: skills/guidelines/{domain}/SKILL.md
```

````
