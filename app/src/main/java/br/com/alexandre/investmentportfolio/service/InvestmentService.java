package br.com.alexandre.investmentportfolio.service;

import java.util.List;

import br.com.alexandre.investmentportfolio.entity.Investment;

public interface InvestmentService {
    List<Investment> getAllInvestments();
}
