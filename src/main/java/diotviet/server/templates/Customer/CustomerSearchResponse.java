package diotviet.server.templates.Customer;

import diotviet.server.views.Customer.CustomerSearchView;
import org.springframework.data.domain.Page;

public record CustomerSearchResponse(Page<CustomerSearchView> items) {
}
