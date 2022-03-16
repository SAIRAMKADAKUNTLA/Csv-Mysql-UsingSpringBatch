package com.bridgelabz.springbatchimplementation.configuration;

import com.bridgelabz.springbatchimplementation.model.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Employee> reader(){
        FlatFileItemReader<Employee> reader=new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("Records.csv"));
        reader.setLineMapper(getLineMapper());
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public LineMapper<Employee> getLineMapper() {
        DefaultLineMapper<Employee> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"Emp ID", "Name Prefix", "First Name", "Last Name", "Gender"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2, 4, 5});

        BeanWrapperFieldSetMapper<Employee> feildSetMapper = new BeanWrapperFieldSetMapper<>();
        feildSetMapper.setTargetType(Employee.class);

        mapper.setLineTokenizer(lineTokenizer);
        mapper.setFieldSetMapper(feildSetMapper);
        return mapper;
    }
    @Bean
    public EmployeeItemProcessor itemProcessor(){
        return new EmployeeItemProcessor();
    }
    @Bean
    public JdbcBatchItemWriter<Employee>writer(){
        JdbcBatchItemWriter<Employee>writer=new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
        writer.setSql("insert into record(empId,namePrefix,firstName,LastName,gender) " +
                "values(:empId,:namePrefix,:firstName,:LastName,:gender)");
        writer.setDataSource(this.dataSource);
        return writer;
    }
    @Bean
    public Job job(){
        return jobBuilderFactory.get("job").incrementer(new RunIdIncrementer())
                .flow(step()).end().build();
    }

    public Step step() {
        return stepBuilderFactory.get("step").<Employee,Employee>chunk(10)
                .reader(reader()).processor(itemProcessor()).writer(writer()).build();

    }
}
