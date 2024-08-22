CREATE TABLE Currencies(
                           ID INTEGER PRIMARY KEY AUTOINCREMENT,
                           Code Varchar UNIQUE,
                           FullName Varchar,
                           Sign Varchar
);

CREATE TABLE ExchangeRates(
                              ID INTEGER PRIMARY KEY AUTOINCREMENT,
                              BaseCurrencyId INT,
                              TargetCurrencyId INT,
                              Rate DECIMAL(6),
                              FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
                              FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID),
                              UNIQUE (BaseCurrencyId,TargetCurrencyId)
);

SELECT * FROM Currencies WHERE Code ='USDDD' OR 'a'='a' ;
SELECT ExchangeRates.*, concat(C1.Code,C2.Code) AS CodeCombo, C1.Code AS C1_Code,
       C1.FullName AS C1_Name, C1.Sign AS C1_Sign, C2.Code AS C2_Code,
       C2.FullName AS C2_Name, C2.Sign AS C2_Sign FROM ExchangeRates
JOIN Currencies C1 ON ExchangeRates.BaseCurrencyId = C1.ID
JOIN Currencies C2 ON ExchangeRates.TargetCurrencyId = C2.ID
WHERE CodeCombo='USDRUB';

SELECT ExchangeRates.*, C1.Code, C1.FullName,C1.Sign, C2.Code, C2.FullName,C2.Sign FROM ExchangeRates
JOIN Currencies C1 on ExchangeRates.BaseCurrencyId = C1.ID
JOIN Currencies C2 on C2.ID = ExchangeRates.TargetCurrencyId
WHERE C1.Code='USD' AND C2.Code ='RUB';

UPDATE ExchangeRates
SET Rate = 49.09
WHERE ID=(SELECT ExchangeRates.ID FROM ExchangeRates
        JOIN Currencies C1 ON ExchangeRates.BaseCurrencyId = C1.ID
        JOIN Currencies C2 ON ExchangeRates.TargetCurrencyId = C2.ID
       WHERE CONCAT(C1.Code,C2.Code)='USDRUB');

INSERT INTO Currencies(Code,FullName,Sign)
VALUES
    ('USD', 'US dollar', '$'),
    ('BYN','Belarussian ruble', 'p');

INSERT INTO ExchangeRates(BaseCurrencyId,TargetCurrencyId,Rate)
VALUES
    (2,3,0.588253),
    (4,3,0.305784),
    (1,4,2.12);

DROP TABLE ExchangeRates;