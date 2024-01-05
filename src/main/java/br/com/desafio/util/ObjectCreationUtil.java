package br.com.desafio.util;

import br.com.desafio.DTO.Auth.RegisterDTO;
import br.com.desafio.DTO.Response.AggregatedValueResponse;
import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.entity.AuditItem;
import br.com.desafio.entity.Product;
import br.com.desafio.entity.RefreshToken;
import br.com.desafio.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.history.Revision;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ObjectCreationUtil {

    private static final String UPDATE = "Atualização";
    private static final String INSERT = "Inserção";
    private static final String DELETE = "Deleção";
    private static final Integer PAGE_DEFAULT_SIZE = 5;
    private static final Integer INITIAL_PAGE_DEFAULT = 0;

    public static AuditItem createAuditItem(Revision<Long, Product> revision, String username) {
        AuditItem auditItem = new AuditItem();
        String revisionName = getRevisionName(revision.getMetadata().getRevisionType().toString());
        auditItem.setAuditId(revision.getMetadata().getRevisionNumber().hashCode());
        auditItem.setProductId(revision.getEntity().getProductId());
        auditItem.setAction(revisionName);
        auditItem.setDate(!revisionName.equals(UPDATE) ? revision.getEntity().getEntryDate() : revision.getEntity().getUpdatedAt());
        auditItem.setUsername(username);
        return auditItem;
    }

    private static String getRevisionName(String revisionName) {
        return switch (revisionName) {
            case "INSERT" -> INSERT;
            case "UPDATE" -> UPDATE;
            case "DELETE" -> DELETE;
            default -> revisionName;
        };
    }

    public static List<AggregatedValueResponse> createAggregatedValueResponses(Page<ProductResponse> products) {
        List<AggregatedValueResponse> valueResponseList = new ArrayList<>();
        for(ProductResponse productResponse: products.getContent()) {
            valueResponseList.add(ObjectCreationUtil.createAggregatedValueResponse(productResponse));
        }

        return valueResponseList;
    }


    protected static AggregatedValueResponse createAggregatedValueResponse(ProductResponse productResponse) {
        return AggregatedValueResponse
                .builder()
                .productId(productResponse.getProductId())
                .cost(productResponse.getCost())
                .totalCost(getTotalCost(productResponse.getCost(), productResponse.getQuantity()))
                .forecast(getTotalReturn(productResponse.getQuantity(), productResponse.getRevenueValue()))
                .build();
    }

    protected static BigDecimal getTotalReturn(Long quantity, BigDecimal revenueValue) {
        return BigDecimal.valueOf(quantity).multiply(revenueValue);
    }

    protected static BigDecimal getTotalCost(BigDecimal cost, Long quantity) {
        return BigDecimal.valueOf(quantity).multiply(cost);
    }

    public static Page<AggregatedValueResponse> createPage(List<AggregatedValueResponse> list, Optional<Integer> page, Optional<Integer> pageSize, Optional<String> sortBy,
                                                     Optional<Sort.Direction> sort) {
        int pageBegin = page.orElse(INITIAL_PAGE_DEFAULT) * pageSize.orElse(PAGE_DEFAULT_SIZE);
        int pageEnd = Math.min(pageBegin + pageSize.orElse(PAGE_DEFAULT_SIZE), list.size());

        List<AggregatedValueResponse> pageElements = list.subList(pageBegin, pageEnd);

        return new PageImpl<>(pageElements, getPageable(page, sortBy, pageSize, sort), list.size());
    }

    public static Pageable getPageable(Optional<Integer> page,
                                 Optional<String> sortBy,
                                 Optional<Integer> pageSize,
                                 Optional<Sort.Direction> sort) {
        return PageRequest.of(page.orElse(INITIAL_PAGE_DEFAULT), pageSize.orElse(PAGE_DEFAULT_SIZE), sort.orElse(Sort.Direction.ASC), sortBy.orElse("productId"));
    }

    public static Product buildProductExample(Optional<Long> productId,
                                 Optional<String> name,
                                 Optional<LocalDateTime> entryDate,
                                 Optional<Boolean> active,
                                 Optional<String> sku,
                                 Optional<Long> categoryId,
                                 Optional<BigDecimal> cost,
                                 Optional<BigDecimal> icms,
                                 Optional<BigDecimal> revenueValue,
                                 Optional<Long> userId,
                                 Optional<Long> quantity) {
        return Product.builder()
                .userId(userId.orElse(null))
                .activeProduct(active.orElse(true))
                .entryDate(entryDate.orElse(null))
                .ICMS(icms.orElse(null))
                .productId(productId.orElse(null))
                .name(name.orElse(null))
                .categoryId(categoryId.orElse(null))
                .revenueValue(revenueValue.orElse(null))
                .cost(cost.orElse(null))
                .SKU(sku.orElse(null))
                .quantity(quantity.orElse(null))
                .build();
    }

    public static User createUser(RegisterDTO registerDTO) {
        return User.builder()
                .email(registerDTO.email())
                .password(encryptPassword(registerDTO.password()))
                .userRole(registerDTO.role())
                .build();
    }

    public static RefreshToken createRefreshToken(int tokenExpirationTime, User user, String token) {
        return RefreshToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusMinutes(tokenExpirationTime).toInstant(ZoneOffset.of("-03:00")))
                .userInfo(user.getId())
                .userRole(user.getUserRole())
                .email(user.getEmail())
                .build();
    }

    private static String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
