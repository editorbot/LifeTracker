package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Habit type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Habits", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Habit implements Model {
  public static final QueryField ID = field("Habit", "id");
  public static final QueryField TITLE = field("Habit", "title");
  public static final QueryField PRIORITY = field("Habit", "priority");
  public static final QueryField CATEGORY = field("Habit", "category");
  public static final QueryField IS_COMPLETED = field("Habit", "isCompleted");
  public static final QueryField DATE = field("Habit", "date");
  public static final QueryField START_TIME = field("Habit", "startTime");
  public static final QueryField END_TIME = field("Habit", "endTime");
  public static final QueryField CREATED_AT = field("Habit", "createdAt");
  public static final QueryField COMPLETED_AT = field("Habit", "completedAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String", isRequired = true) String priority;
  private final @ModelField(targetType="String", isRequired = true) String category;
  private final @ModelField(targetType="Boolean", isRequired = true) Boolean isCompleted;
  private final @ModelField(targetType="String", isRequired = true) String date;
  private final @ModelField(targetType="String") String startTime;
  private final @ModelField(targetType="String") String endTime;
  private final @ModelField(targetType="String", isRequired = true) String createdAt;
  private final @ModelField(targetType="String") String completedAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getPriority() {
      return priority;
  }
  
  public String getCategory() {
      return category;
  }
  
  public Boolean getIsCompleted() {
      return isCompleted;
  }
  
  public String getDate() {
      return date;
  }
  
  public String getStartTime() {
      return startTime;
  }
  
  public String getEndTime() {
      return endTime;
  }
  
  public String getCreatedAt() {
      return createdAt;
  }
  
  public String getCompletedAt() {
      return completedAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Habit(String id, String title, String priority, String category, Boolean isCompleted, String date, String startTime, String endTime, String createdAt, String completedAt) {
    this.id = id;
    this.title = title;
    this.priority = priority;
    this.category = category;
    this.isCompleted = isCompleted;
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.createdAt = createdAt;
    this.completedAt = completedAt;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Habit habit = (Habit) obj;
      return ObjectsCompat.equals(getId(), habit.getId()) &&
              ObjectsCompat.equals(getTitle(), habit.getTitle()) &&
              ObjectsCompat.equals(getPriority(), habit.getPriority()) &&
              ObjectsCompat.equals(getCategory(), habit.getCategory()) &&
              ObjectsCompat.equals(getIsCompleted(), habit.getIsCompleted()) &&
              ObjectsCompat.equals(getDate(), habit.getDate()) &&
              ObjectsCompat.equals(getStartTime(), habit.getStartTime()) &&
              ObjectsCompat.equals(getEndTime(), habit.getEndTime()) &&
              ObjectsCompat.equals(getCreatedAt(), habit.getCreatedAt()) &&
              ObjectsCompat.equals(getCompletedAt(), habit.getCompletedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), habit.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTitle())
      .append(getPriority())
      .append(getCategory())
      .append(getIsCompleted())
      .append(getDate())
      .append(getStartTime())
      .append(getEndTime())
      .append(getCreatedAt())
      .append(getCompletedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Habit {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("priority=" + String.valueOf(getPriority()) + ", ")
      .append("category=" + String.valueOf(getCategory()) + ", ")
      .append("isCompleted=" + String.valueOf(getIsCompleted()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("startTime=" + String.valueOf(getStartTime()) + ", ")
      .append("endTime=" + String.valueOf(getEndTime()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("completedAt=" + String.valueOf(getCompletedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static TitleStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Habit justId(String id) {
    return new Habit(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      title,
      priority,
      category,
      isCompleted,
      date,
      startTime,
      endTime,
      createdAt,
      completedAt);
  }
  public interface TitleStep {
    PriorityStep title(String title);
  }
  

  public interface PriorityStep {
    CategoryStep priority(String priority);
  }
  

  public interface CategoryStep {
    IsCompletedStep category(String category);
  }
  

  public interface IsCompletedStep {
    DateStep isCompleted(Boolean isCompleted);
  }
  

  public interface DateStep {
    CreatedAtStep date(String date);
  }
  

  public interface CreatedAtStep {
    BuildStep createdAt(String createdAt);
  }
  

  public interface BuildStep {
    Habit build();
    BuildStep id(String id);
    BuildStep startTime(String startTime);
    BuildStep endTime(String endTime);
    BuildStep completedAt(String completedAt);
  }
  

  public static class Builder implements TitleStep, PriorityStep, CategoryStep, IsCompletedStep, DateStep, CreatedAtStep, BuildStep {
    private String id;
    private String title;
    private String priority;
    private String category;
    private Boolean isCompleted;
    private String date;
    private String createdAt;
    private String startTime;
    private String endTime;
    private String completedAt;
    public Builder() {
      
    }
    
    private Builder(String id, String title, String priority, String category, Boolean isCompleted, String date, String startTime, String endTime, String createdAt, String completedAt) {
      this.id = id;
      this.title = title;
      this.priority = priority;
      this.category = category;
      this.isCompleted = isCompleted;
      this.date = date;
      this.startTime = startTime;
      this.endTime = endTime;
      this.createdAt = createdAt;
      this.completedAt = completedAt;
    }
    
    @Override
     public Habit build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Habit(
          id,
          title,
          priority,
          category,
          isCompleted,
          date,
          startTime,
          endTime,
          createdAt,
          completedAt);
    }
    
    @Override
     public PriorityStep title(String title) {
        Objects.requireNonNull(title);
        this.title = title;
        return this;
    }
    
    @Override
     public CategoryStep priority(String priority) {
        Objects.requireNonNull(priority);
        this.priority = priority;
        return this;
    }
    
    @Override
     public IsCompletedStep category(String category) {
        Objects.requireNonNull(category);
        this.category = category;
        return this;
    }
    
    @Override
     public DateStep isCompleted(Boolean isCompleted) {
        Objects.requireNonNull(isCompleted);
        this.isCompleted = isCompleted;
        return this;
    }
    
    @Override
     public CreatedAtStep date(String date) {
        Objects.requireNonNull(date);
        this.date = date;
        return this;
    }
    
    @Override
     public BuildStep createdAt(String createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
        return this;
    }
    
    @Override
     public BuildStep startTime(String startTime) {
        this.startTime = startTime;
        return this;
    }
    
    @Override
     public BuildStep endTime(String endTime) {
        this.endTime = endTime;
        return this;
    }
    
    @Override
     public BuildStep completedAt(String completedAt) {
        this.completedAt = completedAt;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String title, String priority, String category, Boolean isCompleted, String date, String startTime, String endTime, String createdAt, String completedAt) {
      super(id, title, priority, category, isCompleted, date, startTime, endTime, createdAt, completedAt);
      Objects.requireNonNull(title);
      Objects.requireNonNull(priority);
      Objects.requireNonNull(category);
      Objects.requireNonNull(isCompleted);
      Objects.requireNonNull(date);
      Objects.requireNonNull(createdAt);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder priority(String priority) {
      return (CopyOfBuilder) super.priority(priority);
    }
    
    @Override
     public CopyOfBuilder category(String category) {
      return (CopyOfBuilder) super.category(category);
    }
    
    @Override
     public CopyOfBuilder isCompleted(Boolean isCompleted) {
      return (CopyOfBuilder) super.isCompleted(isCompleted);
    }
    
    @Override
     public CopyOfBuilder date(String date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder createdAt(String createdAt) {
      return (CopyOfBuilder) super.createdAt(createdAt);
    }
    
    @Override
     public CopyOfBuilder startTime(String startTime) {
      return (CopyOfBuilder) super.startTime(startTime);
    }
    
    @Override
     public CopyOfBuilder endTime(String endTime) {
      return (CopyOfBuilder) super.endTime(endTime);
    }
    
    @Override
     public CopyOfBuilder completedAt(String completedAt) {
      return (CopyOfBuilder) super.completedAt(completedAt);
    }
  }
  

  public static class HabitIdentifier extends ModelIdentifier<Habit> {
    private static final long serialVersionUID = 1L;
    public HabitIdentifier(String id) {
      super(id);
    }
  }
  
}
