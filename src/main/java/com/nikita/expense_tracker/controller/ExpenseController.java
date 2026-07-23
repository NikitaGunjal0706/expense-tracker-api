
package com.nikita.expense_tracker.controller;

import com.nikita.expense_tracker.model.Expense;
import com.nikita.expense_tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.nikita.expense_tracker.service.GeminiService;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/ask")
    public String askExpenses(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        List<Expense> expenses = expenseService.getAllExpenses();
        return geminiService.askAboutExpenses(question, expenses);
    }
    
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public Expense addExpense(@RequestBody Expense expense) {
        return expenseService.addExpense(expense);
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
        return expenseService.updateExpense(id, expense);
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "Expense with id " + id + " deleted successfully.";
    }

    @GetMapping("/category/{category}")
    public List<Expense> getByCategory(@PathVariable String category) {
        return expenseService.getExpensesByCategory(category);
    }

    @GetMapping("/total")
    public Double getTotalExpenses() {
        return expenseService.getTotalExpenses();
    }
}