package br.com.alexandre.investmentportfolio.service;

import java.util.List;

import br.com.alexandre.investmentportfolio.entity.Company;
import br.com.alexandre.investmentportfolio.entity.Investment;

public interface CompanyService {
    List<Company> getAllCompanies();
}
