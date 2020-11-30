package models.Contents.charts;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.Contents.Content;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Chart extends Content {
    List<Data> data;
}
