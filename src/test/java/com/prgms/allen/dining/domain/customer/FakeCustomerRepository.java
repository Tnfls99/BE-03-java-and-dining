package com.prgms.allen.dining.domain.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.prgms.allen.dining.domain.customer.entity.Customer;
import com.prgms.allen.dining.domain.customer.entity.CustomerType;

public class FakeCustomerRepository implements CustomerRepository {

	private final List<Customer> customers = new ArrayList<>();

	@Override
	public List<Customer> findAll() {
		return customers;
	}

	@Override
	public List<Customer> findAll(Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<Customer> findAll(Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Customer> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		return customers.size();
	}

	@Override
	public void deleteById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Customer entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Customer> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		customers.clear();
	}

	@Override
	public <S extends Customer> S save(S entity) {
		Customer customer = new Customer(
			count() + 1,
			entity.getNickname(),
			entity.getName(),
			entity.getPhone(),
			entity.getPassword(),
			entity.getCustomerType()
		);
		customers.add(customer);
		return (S)customer;
	}

	@Override
	public <S extends Customer> List<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Customer> findById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> S saveAndFlush(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> List<S> saveAllAndFlush(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch(Iterable<Customer> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Customer getOne(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Customer getById(Long aLong) {
		return customers.stream()
			.filter(customer -> aLong.equals(customer.getId()))
			.findAny()
			.orElseThrow(UnsupportedOperationException::new);
	}

	@Override
	public Customer getReferenceById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> long count(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Customer> findByIdAndCustomerType(Long id, CustomerType customerType) {
		return customers.stream()
			.filter(customer -> id.equals(customer.getId()))
			.filter(customer -> customerType.equals(customer.getCustomerType()))
			.findAny();
	}
}
