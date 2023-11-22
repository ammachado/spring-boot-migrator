/*
 * Copyright 2021 - 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sbm.boot;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.sbm.SbmConstants.LS;

public class MarkdownRenderer {
    private final List<SectionBuilder.Section> sections = new ArrayList<>();
    private OverviewSectionBuilder.OverviewSection overviewSection;

    public void addSection(SectionBuilder.Section section) {
        this.sections.add(section);
    }

    public String render() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(overviewSection.render());
        sections.forEach(s -> {
            stringBuilder.append(renderSection(s));
        });
        return stringBuilder.toString();
    }

    private String renderSection(SectionBuilder.Section s) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("== ").append(s.getTitle()).append(LS).append(LS);
        s.getParagraphs().forEach(p -> stringBuilder.append(p).append("<br>").append(LS).append(LS));
        stringBuilder.append(renderRelevanceSection(s.getRelevanceSection()));
        stringBuilder.append(renderTodoSection(s.getTodoSection()));
        stringBuilder.append(LS);
        return stringBuilder.toString();
    }

    private String renderTodoSection(SectionBuilder.TodoSection todoSection) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("=== ").append("Todo").append(LS).append(LS);
        todoSection.getTodos().forEach(todo -> {
            stringBuilder.append("- [ ] ").append(todo).append(LS);
        });
        return stringBuilder.toString();
    }

    private String renderRelevanceSection(SectionBuilder.RelevanceSection relevanceSection) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("=== ").append("Relevance").append(LS).append(LS);
        relevanceSection.getParagraphs().forEach(p -> {
            stringBuilder.append(p).append(LS);
        });
        stringBuilder.append(LS);
        return stringBuilder.toString();
    }

    public void addSection(OverviewSectionBuilder.OverviewSection overview) {
        this.overviewSection = overview;
    }
}
