package models.Contents.charts;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.Contents.BasicContent;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BasicChart extends BasicContent {
    List<Data> data;
}
