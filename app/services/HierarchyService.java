package services;

import com.google.inject.Inject;
import models.Dashboard;
import play.libs.concurrent.HttpExecutionContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HierarchyService {

    @Inject
    HttpExecutionContext ec;

    public CompletableFuture<List<Dashboard>> hierarchy (List<Dashboard> input) {
        return CompletableFuture.supplyAsync(() -> input.stream()
                .filter(x -> x.getParentId() == null)
                .map((next) -> hierarchy(next, input))
                .collect(Collectors.toList()), ec.current());
    }

    public Dashboard hierarchy (Dashboard company, List<Dashboard> input) {
        List<Dashboard> children = input
                .stream()
                .filter(x -> company.getId().equals(x.getParentId()))
                .map((x) -> hierarchy(x, input))
                .collect(Collectors.toList());
        company.setChildren(children);
        return company;
    }
}
