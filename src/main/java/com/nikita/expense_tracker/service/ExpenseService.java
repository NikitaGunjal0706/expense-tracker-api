package com.nikita.expense_tracker.service;

import com.nikita.expense_tracker.model.Expense;
import com.nikita.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense addExpense(Expense expense) {

        // Rule 1: Don't allow negative amounts
        if (expense.getAmount() == null || expense.getAmount() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        // Rule 2: Auto-categorize if category is missing/blank
        if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
            expense.setCategory("Uncategorized");
        }

        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        Expense existing = getExpenseById(id);

        if (updatedExpense.getAmount() != null && updatedExpense.getAmount() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());

        if (updatedExpense.getCategory() == null || updatedExpense.getCategory().trim().isEmpty()) {
            existing.setCategory("Uncategorized");
        } else {
            existing.setCategory(updatedExpense.getCategory());
        }

        existing.setDate(updatedExpense.getDate());
        return expenseRepository.save(existing);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenseRepository.findByCategoryIgnoreCase(category);
    }

    // Rule 3: Calculate total of all expenses
    public Double getTotalExpenses() {
        List<Expense> allExpenses = expenseRepository.findAll();
        double total = 0.0;
        for (Expense expense : allExpenses) {
            total += expense.getAmount();
        }
        return total;
    }
}